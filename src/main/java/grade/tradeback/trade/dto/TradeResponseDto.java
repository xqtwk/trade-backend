package grade.tradeback.trade.dto;

import grade.tradeback.catalog.asset.Asset;
import grade.tradeback.trade.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeResponseDto {
    private Long Id; // ID of the buyer
    private String senderUsername;
    private String receiverUsername;
    private int amount;
    private boolean senderConfirmed;
    private boolean receiverConfirmed;
    private double sum;
    private TradeStatus status;
    private Long assetId; // Just the ID, instead of the whole Asset entity
}