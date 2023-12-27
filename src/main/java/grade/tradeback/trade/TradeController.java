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
import java.util.Objects;

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
            if (tradeRequestDto.getAmount() <= 0) {
                tradeService.sendErrorMessage(principal.getName(), "Neteisingas kiekis");
                return;
            }
            if (
                asset.getAmount() == null
                || asset.getAmount() >= tradeRequestDto.getAmount()
            ) {
                Trade trade = tradeService.createAndSaveTrade(asset, tradeRequestDto);
                if (trade != null) {
                    tradeService.notifySellerAboutTrade(trade);
                    tradeService.sendTradeIdToBuyer(principal.getName(), trade.getId());
                }
            } else {
                tradeService.sendErrorMessage(principal.getName(), "Neteisingas kiekis");
            }
        }
    }


    @MessageMapping("/trade/confirm")
    public void confirmTrade(TradeConfirmationDto tradeConfirmationDto, Principal connectedUser) {
        if (Objects.equals(connectedUser.getName(), tradeConfirmationDto.getUsername())) {
            tradeService.confirmTrade(tradeConfirmationDto);
        }
    }
    @MessageMapping("/trade/cancel")
    public void cancelTrade(TradeConfirmationDto tradeConfirmationDto, Principal connectedUser) {
        if (Objects.equals(connectedUser.getName(), tradeConfirmationDto.getUsername())) {
            tradeService.cancelTrade(tradeConfirmationDto);
        }
    }

    @GetMapping("/trade-list")
    public ResponseEntity<List<Trade>> getTradeList(Principal currentUser) {
        List<Trade> chatList = tradeService.getTradeListForUser(currentUser.getName());
        return ResponseEntity.ok(chatList);
    }
    @GetMapping("/sales")
    public ResponseEntity<List<Trade>> getSalesList(Principal currentUser) {
        List<Trade> chatList = tradeService.getSalesListForUser(currentUser.getName());
        return ResponseEntity.ok(chatList);
    }
    @GetMapping("/purchases")
    public ResponseEntity<List<Trade>> getPurchasesList(Principal currentUser) {
        List<Trade> chatList = tradeService.getPurchasesListForUser(currentUser.getName());
        return ResponseEntity.ok(chatList);
    }

    @GetMapping("/trade/{id}")
    public ResponseEntity<TradeResponseDto> getTrade(@PathVariable Long id, Principal currentUser) {
        return tradeService.getTradeForUser(id, currentUser.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

}
