package grade.tradeback.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/top-up")
    public String topUpBalance(@RequestBody PaymentRequest paymentRequest) {
        String token = paymentRequest.getTokenId();
        if(token == null || token.isEmpty()){
            return "No Token ID provided!";
        }
        return paymentService.createCharge(paymentRequest.getAmount(),
                paymentRequest.getUserId(),
                paymentRequest.getTokenId());
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody CustomAccountRequest customAccountRequest) {
        try {
            Account account = paymentService.createCustomAccountWithDetails(customAccountRequest);
            return ResponseEntity.ok(account);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating Stripe account: " + e.getMessage());
        }
    }
}
