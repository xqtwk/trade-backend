package grade.tradeback.trade.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRequestDto {
    private Long buyerUserId; // ID of the buyer
    private Long assetId; // ID of the asset being bought
    private int amount; // Am
}
