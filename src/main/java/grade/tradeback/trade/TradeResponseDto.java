package grade.tradeback.trade;

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
    private double amount;
    private boolean senderConfirmed;
    private boolean receiverConfirmed;
}