package grade.tradeback.trade;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRequestDto {
    private Long buyerUserId; // ID of the buyer
    private Long assetId; // ID of the asset being bought
    private double amount; // Am
}
