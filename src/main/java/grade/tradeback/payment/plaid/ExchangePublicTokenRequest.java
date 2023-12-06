package grade.tradeback.payment.plaid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangePublicTokenRequest {
    private String publicToken;
    private String username;
}
