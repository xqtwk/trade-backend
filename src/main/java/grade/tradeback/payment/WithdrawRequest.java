package grade.tradeback.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest {
    private long amount;
    private String connectedAccountId;
    private String userId;
    private String tokenId;
}
