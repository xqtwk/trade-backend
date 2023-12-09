package grade.tradeback.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import grade.tradeback.payment.stripe.dto.CustomAccountCreationResponse;
import grade.tradeback.payment.stripe.dto.CustomAccountRequest;
import grade.tradeback.payment.stripe.dto.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class StripeController {
    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/top-up")
    public String topUpBalance(@RequestBody PaymentRequest paymentRequest) {
        String token = paymentRequest.getTokenId();
        if(token == null || token.isEmpty()){
            return "No Token ID provided!";
        }
        return stripeService.createCharge(paymentRequest.getAmount(),
                paymentRequest.getUserId(),
                paymentRequest.getTokenId());
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody CustomAccountRequest customAccountRequest) throws StripeException {
        System.out.println(customAccountRequest);
            // TODO: EDIT THIS SHIATT
            CustomAccountCreationResponse customAccountCreationResponse = stripeService.createCustomAccountWithDetails(customAccountRequest);
            // ResponseEntity.ok(customAccountCreationResponse);
            return ResponseEntity.ok(
                    stripeService
                            .createStripeAccountAndGenerateOnboardingLink(customAccountCreationResponse.getId()));
    }

}
