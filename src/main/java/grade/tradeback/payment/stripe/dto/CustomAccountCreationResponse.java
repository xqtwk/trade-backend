package grade.tradeback.payment.stripe.dto;

import com.stripe.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomAccountCreationResponse {
    private String id;

    public CustomAccountCreationResponse(Account stripeAccount) {
        this.id = stripeAccount.getId();
    }
}
