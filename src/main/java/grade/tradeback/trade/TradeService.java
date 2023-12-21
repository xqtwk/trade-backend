package grade.tradeback.trade;

import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    public Trade createTrade(Long sellerUserId, Long buyerUserId, double amount) {
        // Fetch usernames from user IDs
        String sellerUsername = userService.getUsernameById(sellerUserId);
        String buyerUsername = userService.getUsernameById(buyerUserId);

        Trade trade = Trade.builder()
                .senderUsername(sellerUsername)
                .receiverUsername(buyerUsername)
                .amount(amount)
                .senderConfirmed(false)
                .receiverConfirmed(false)
                .build();

        return tradeRepository.save(trade);
    }

    public Optional<Trade> confirmReceiver(String tradeId) {
        return tradeRepository.findById(Long.parseLong(tradeId)).map(trade -> {
            if (trade.isSenderConfirmed()) {  // Ensure receiver has already confirmed
                trade.setReceiverConfirmed(true);
                userService.addBalance(trade.getSenderUsername(), trade.getAmount());
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
    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    public List<Trade> getTradeListForUser(String username) {
        return tradeRepository.findBySenderUsernameOrReceiverUsername(username, username);
    }

    public Optional<TradeResponseDto> getTradeForUser(Long tradeId, String username) {
        return tradeRepository.findById(tradeId).filter(trade ->
                trade.getSenderUsername().equals(username) || trade.getReceiverUsername().equals(username)
        ).map(trade -> new TradeResponseDto(
                trade.getId(),
                trade.getSenderUsername(),
                trade.getReceiverUsername(),
                // Assuming you have a way to get buyerUserId and assetId
                // trade.getBuyerUserId(),
                // trade.getAssetId(),
                trade.getAmount(),
                trade.isSenderConfirmed(),
                trade.isReceiverConfirmed()
        ));
    }
}
