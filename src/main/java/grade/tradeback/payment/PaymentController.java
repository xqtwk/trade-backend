package grade.tradeback.payments;

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
    @PostMapping("/payout")
    public String payout(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.payout(paymentRequest.getAmount(), paymentRequest.getUserId());
    }

}
