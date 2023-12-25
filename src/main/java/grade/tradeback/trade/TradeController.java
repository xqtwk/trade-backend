package grade.tradeback.trade;
import grade.tradeback.catalog.asset.Asset;
import grade.tradeback.catalog.asset.AssetService;
import grade.tradeback.trade.dto.TradeConfirmationDto;
import grade.tradeback.trade.dto.TradeRequestDto;
import grade.tradeback.trade.dto.TradeResponseDto;
import grade.tradeback.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TradeController {
    private final SimpMessagingTemplate messagingTemplate;
    private final TradeService tradeService;
    private final AssetService assetService; // Assuming you have a service to handle asset-related operations
    private final TradeRepository tradeRepository;
    private final UserService userService;
    @MessageMapping("/trade/initiate")
    public void initiateTrade(TradeRequestDto tradeRequestDto, Principal principal) {
        Asset asset = assetService.findById(tradeRequestDto.getAssetId()).orElse(null);
        if (asset != null) {
            if (asset.getUser().getId() == tradeRequestDto.getBuyerUserId()) {
                tradeService.sendErrorMessage(principal.getName(), "Deja, negalima pirkti savo pačių pateiktos prekės.");
                return;
            }
            if (asset.getAmount() == null || asset.getAmount() >= tradeRequestDto.getAmount()) {
                Trade trade = tradeService.createAndSaveTrade(asset, tradeRequestDto);
                tradeService.notifySellerAboutTrade(trade);
                tradeService.sendTradeIdToBuyer(principal.getName(), trade.getId());
            }
        }
    }


    @MessageMapping("/trade/confirm")
    public void confirmTrade(TradeConfirmationDto tradeConfirmationDto) {
        Trade trade = tradeService.findById(tradeConfirmationDto.getTradeId()).orElse(null);
        if (trade != null) {
            System.out.println("Receiver Username: " + trade.getReceiverUsername());
            System.out.println("DTO Username: " + tradeConfirmationDto.getUsername());
            if (trade.getReceiverUsername().equals(tradeConfirmationDto.getUsername())) {
                System.out.println("first receiver if passed");
                if (trade.isSenderConfirmed()) {
                    System.out.println("receiver confirmed");
                    tradeService.confirmReceiver(tradeConfirmationDto.getTradeId().toString());
                }
            } else if (trade.getSenderUsername().equals(tradeConfirmationDto.getUsername())) {
                tradeService.confirmSender(tradeConfirmationDto.getTradeId().toString());
            }
            Trade updatedTrade = tradeService.findById(trade.getId()).orElse(null);
            if (updatedTrade != null) {
                messagingTemplate.convertAndSendToUser(updatedTrade.getSenderUsername(), "/queue/trade", updatedTrade);
                messagingTemplate.convertAndSendToUser(updatedTrade.getReceiverUsername(), "/queue/trade", updatedTrade);
            }
        }
    }

    @GetMapping("/trade-list/{username}")
    public ResponseEntity<List<Trade>> getTradeList(Principal currentUser) {
        List<Trade> chatList = tradeService.getTradeListForUser(currentUser.getName());
        return ResponseEntity.ok(chatList);
    }

    @GetMapping("/trade/{id}")
    public ResponseEntity<TradeResponseDto> getTrade(@PathVariable Long id, Principal currentUser) {
        return tradeService.getTradeForUser(id, currentUser.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

}
