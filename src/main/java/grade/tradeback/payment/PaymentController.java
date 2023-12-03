package grade.tradeback.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import grade.tradeback.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.PaymentMethod;

import java.util.Map;

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
    @PostMapping("/customer")
    public ResponseEntity<?> createCustomer(@RequestBody User user) {
        try {
            Customer customer = paymentService.createStripeCustomer(user.getEmail());
            return ResponseEntity.ok(Map.of("id", customer.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating customer: " + e.getMessage());
        }
    }

    @PostMapping("/customer/{customerId}/payment-method")
    public String addPaymentMethod(@PathVariable String customerId, @RequestBody String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = paymentService.attachPaymentMethodToCustomer(customerId, paymentMethodId);
            return paymentMethod.getId();
        } catch (Exception e) {
            return "Error attaching payment method: " + e.getMessage();
        }
    }
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody WithdrawRequest transferRequest) {
        try {
            String transferId = paymentService.createTransfer(transferRequest.getAmount(), transferRequest.getConnectedAccountId());
            return ResponseEntity.ok(transferId);
        } catch (StripeException e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in transfer: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions that might occur
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("General error: " + e.getMessage());
        }
    }
}
