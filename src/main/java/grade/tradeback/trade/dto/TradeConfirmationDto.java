package grade.tradeback.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeConfirmationDto {
    private Long tradeId;
    private String username;
}
