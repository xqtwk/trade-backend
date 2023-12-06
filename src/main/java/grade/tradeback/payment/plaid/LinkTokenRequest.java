package grade.tradeback.payment.plaid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkTokenRequest {
    private String clientId;
    private String secret;
    private String linkToken;
}