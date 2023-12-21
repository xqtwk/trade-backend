package grade.tradeback.trade;
import grade.tradeback.catalog.asset.Asset;
import grade.tradeback.catalog.asset.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
    @MessageMapping("/trade/initiate")
    public void initiateTrade(TradeRequestDto tradeRequestDto) {
        // Find asset and seller details based on assetId
        Asset asset = assetService.findById(tradeRequestDto.getAssetId()).orElse(null);
        if (asset != null) {
            // Create a new trade
            Trade trade = tradeService.createTrade(
                    asset.getUser().getId(), // Seller's user ID
                    tradeRequestDto.getBuyerUserId(), // Buyer's user ID
                    tradeRequestDto.getAmount());

            // Notify the seller about the trade request
            messagingTemplate.convertAndSendToUser(
                    asset.getUser().getUsername(),
                    "/queue/trade",
                    trade  // Send the trade details to the seller
            );
        }
        // convert method to string and send tradeid to front for redirection
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
            System.out.println("no shit passed");
            // Notify both parties about the updated trade state
            messagingTemplate.convertAndSendToUser(trade.getSenderUsername(), "/queue/trade", trade);
            messagingTemplate.convertAndSendToUser(trade.getReceiverUsername(), "/queue/trade", trade);
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
