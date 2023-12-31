package grade.tradeback.trade;

import grade.tradeback.catalog.asset.Asset;
import grade.tradeback.catalog.asset.AssetRepository;
import grade.tradeback.catalog.asset.AssetService;
import grade.tradeback.catalog.assetType.AssetTypeType;
import grade.tradeback.trade.dto.TradeRequestDto;
import grade.tradeback.trade.dto.TradeResponseDto;
import grade.tradeback.trade.dto.TradeConfirmationDto;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
import grade.tradeback.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final AssetService assetService;
    private final AssetRepository assetRepository;

    public Trade createAndSaveTrade(Asset asset, TradeRequestDto tradeRequestDto) {
        String sellerUsername = userService.getUsernameById(asset.getUser().getId());
        String buyerUsername = userService.getUsernameById(tradeRequestDto.getBuyerUserId());
        double totalCost = tradeRequestDto.getAmount() * asset.getPrice();
        User buyer = userRepository.findById(tradeRequestDto.getBuyerUserId()).orElse(null);
        if (buyer == null || buyer.getBalance() <= totalCost) {
            sendErrorMessage(buyerUsername, "Lėsų skaičius balanse nepakankamas.");
            return null; // Return or handle as appropriate
        }
        try {
            userService.removeBalance(buyerUsername, totalCost);
            if (asset.getAmount() != null) {
                assetService.decreaseAssetAmount(asset.getId(), tradeRequestDto.getAmount());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(buyerUsername, e.getMessage());
            return null; // Return or handle as appropriate
        }
        Trade trade = Trade.builder()
                .senderUsername(sellerUsername)
                .receiverUsername(userService.getUsernameById(tradeRequestDto.getBuyerUserId()))
                .amount(tradeRequestDto.getAmount())
                .sum(tradeRequestDto.getAmount() * asset.getPrice())
                .senderConfirmed(false)
                .receiverConfirmed(false)
                .status(TradeStatus.ACTIVE)
                .asset(asset)
                .build();
        return tradeRepository.save(trade);
    }

    public void notifySellerAboutTrade(Trade trade) {
        messagingTemplate.convertAndSendToUser(trade.getSenderUsername(), "/queue/trade", trade);
    }

    public void sendTradeIdToBuyer(String buyerUsername, Long tradeId) {
        messagingTemplate.convertAndSendToUser(buyerUsername, "/queue/trade-initiation", tradeId.toString());
    }

    public void sendErrorMessage(String username, String errorMessage) {
        messagingTemplate.convertAndSendToUser(username, "/queue/errors", errorMessage);
    }

    public void confirmTrade(TradeConfirmationDto tradeConfirmationDto) {
        Trade trade = findById(tradeConfirmationDto.getTradeId()).orElse(null);
        if (trade != null) {
            TradeStatus status = trade.getStatus();
            if (status != TradeStatus.COMPLETED && status != TradeStatus.CANCELLED) {
                if (trade.getReceiverUsername().equals(tradeConfirmationDto.getUsername()) && trade.isSenderConfirmed()) {
                    confirmReceiver(trade.getId().toString());
                } else if (trade.getSenderUsername().equals(tradeConfirmationDto.getUsername()) && !trade.isSenderConfirmed()) {
                    confirmSender(trade.getId().toString());
                }

                findById(trade.getId()).ifPresent(this::notifyTradeUpdate);
            }
        }
    }



    private void notifyTradeUpdate(Trade updatedTrade) {
        messagingTemplate.convertAndSendToUser(updatedTrade.getSenderUsername(), "/queue/trade", updatedTrade);
        messagingTemplate.convertAndSendToUser(updatedTrade.getReceiverUsername(), "/queue/trade", updatedTrade);
    }

    public Optional<Trade> confirmReceiver(String tradeId) {
        return tradeRepository.findById(Long.parseLong(tradeId)).map(trade -> {
            if (trade.isSenderConfirmed()) {  // Ensure receiver has already confirmed
                trade.setReceiverConfirmed(true);
                trade.setStatus(TradeStatus.COMPLETED);
                userService.addBalance(trade.getSenderUsername(), trade.getSum());
            }
            return tradeRepository.save(trade);
        });
    }

    public Optional<Trade> confirmSender(String tradeId) {
        return tradeRepository.findById(Long.parseLong(tradeId)).map(trade -> {
            trade.setSenderConfirmed(true);
            return tradeRepository.save(trade);
        });
    }

    public void cancelTrade(TradeConfirmationDto tradeCancellationDto) {
        Trade trade = tradeRepository.findById(tradeCancellationDto.getTradeId()).orElse(null);
        if (trade != null) {
            TradeStatus status = trade.getStatus();
            if (status != TradeStatus.COMPLETED && status != TradeStatus.CANCELLED) {
                if (trade.getAsset().getAssetType().getType().equals(AssetTypeType.ITEM)) {
                    if (trade.getReceiverUsername().equals(tradeCancellationDto.getUsername())
                        && Duration.between(trade.getCreationTime(), LocalDateTime.now(ZoneOffset.UTC)).toMinutes() >= 30) {
                        trade.setStatus(TradeStatus.CANCELLED);
                        userService.addBalance(trade.getReceiverUsername(), trade.getSum());
                        tradeRepository.save(trade);
                        assetService.increaseAssetAmount(trade.getAsset().getId(), trade.getAmount());
                    }
                } else if (trade.getAsset().getAssetType().getType().equals(AssetTypeType.SERVICE)) {
                    if (trade.getReceiverUsername().equals(tradeCancellationDto.getUsername())
                        && !trade.isSenderConfirmed()) {
                        trade.setStatus(TradeStatus.CANCELLED);
                        userService.addBalance(trade.getReceiverUsername(), trade.getSum());
                        tradeRepository.save(trade);
                        assetService.increaseAssetAmount(trade.getAsset().getId(), trade.getAmount());
                    }
                }
                if (trade.getReceiverUsername().equals(tradeCancellationDto.getUsername())
                    && Duration.between(trade.getCreationTime(), LocalDateTime.now(ZoneOffset.UTC)).toMinutes() >= 30
                    && trade.getAsset().getAssetType().getType().equals(AssetTypeType.ITEM) && !trade.isSenderConfirmed()) {
                    trade.setStatus(TradeStatus.CANCELLED);
                    userService.addBalance(trade.getReceiverUsername(), trade.getSum());
                    tradeRepository.save(trade);
                    assetService.increaseAssetAmount(trade.getAsset().getId(), trade.getAmount());
                } else if (trade.getSenderUsername().equals(tradeCancellationDto.getUsername())) {
                    trade.setStatus(TradeStatus.CANCELLED);
                    userService.addBalance(trade.getReceiverUsername(), trade.getSum());
                    tradeRepository.save(trade);
                    assetService.increaseAssetAmount(trade.getAsset().getId(), trade.getAmount());
                }

                findById(trade.getId()).ifPresent(this::notifyTradeUpdate);
            }
        }
    }

    public void issueTrade(TradeConfirmationDto tradeCancellationDto) {
        Trade trade = tradeRepository.findById(tradeCancellationDto.getTradeId()).orElse(null);
        if (trade != null) {
            TradeStatus status = trade.getStatus();
            if (status != TradeStatus.COMPLETED && status != TradeStatus.CANCELLED && status != TradeStatus.ISSUE) {
                trade.setStatus(TradeStatus.ISSUE);
                tradeRepository.save(trade);
                findById(trade.getId()).ifPresent(this::notifyTradeUpdate);
            }
        }
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    public List<Trade> getTradeListForUser(String username) {
        return tradeRepository.findBySenderUsernameOrReceiverUsername(username, username);
    }

    public List<Trade> getSalesListForUser(String username) {
        return tradeRepository.findBySenderUsername(username);
    }

    public List<Trade> getPurchasesListForUser(String username) {
        return tradeRepository.findByReceiverUsername(username);
    }

    public Optional<TradeResponseDto> getTradeForUser(Long tradeId, String username) {
        return tradeRepository.findById(tradeId).filter(trade ->
                trade.getSenderUsername().equals(username) || trade.getReceiverUsername().equals(username)
        ).map(trade -> new TradeResponseDto(
                trade.getId(),
                trade.getSenderUsername(),
                trade.getReceiverUsername(),
                trade.getAmount(),
                trade.isSenderConfirmed(),
                trade.isReceiverConfirmed(),
                trade.getSum(),
                trade.getStatus(),
                trade.getAsset().getId(),
                trade.getCreationTime().format(DateTimeFormatter.ISO_DATE_TIME)

        ));
    }
    public List<Trade> getTradeListByStatus(TradeStatus status) {
        return tradeRepository.findByStatus(status);
    }
}
