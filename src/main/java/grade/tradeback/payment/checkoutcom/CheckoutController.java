package grade.tradeback.payment.checkoutcom;

import com.checkout.payments.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/payapi")
public class CheckoutController {
    private final CardPayoutService cardPayoutService;

    public CheckoutController(CardPayoutService cardPayoutService) {
        this.cardPayoutService = cardPayoutService;
    }
    @PostMapping("/add")
    public PaymentStatus sendPaymentData(@RequestBody CheckoutRequest checkoutRequest) throws ExecutionException, InterruptedException {

        //return cardPayoutService.generatePaymentLink(checkoutRequest.getToken(), checkoutRequest.getAmount());
        //return cardPayoutService.requestCardPayment();
        return cardPayoutService.requestPayout();

    }
}
