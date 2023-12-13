package grade.tradeback.trade;
import grade.tradeback.catalog.asset.Asset;
import grade.tradeback.catalog.asset.AssetService;
import grade.tradeback.trade.TradeRequestDto;
import grade.tradeback.chat.chatMessage.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import grade.tradeback.trade.TradeRequestDto;

@Controller
@RequiredArgsConstructor
public class TradeController {
    private final SimpMessagingTemplate messagingTemplate;
    private final TradeService tradeService;
    private final AssetService assetService; // Assuming you have a service to handle asset-related operations

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
    }


    @MessageMapping("/trade/confirm")
    public void confirmTrade(String tradeId, String username) {
        Trade trade = tradeService.findById(Long.parseLong(tradeId)).orElse(null);
        if (trade != null) {
            if (trade.getReceiverUsername().equals(username)) {
                tradeService.confirmReceiver(tradeId);
            } else if (trade.getSenderUsername().equals(username)) {
                tradeService.confirmSender(tradeId);
            }
            // Notify both parties about the updated trade state
            messagingTemplate.convertAndSendToUser(trade.getSenderUsername(), "/queue/trade", trade);
            messagingTemplate.convertAndSendToUser(trade.getReceiverUsername(), "/queue/trade", trade);
        }
    }
}
