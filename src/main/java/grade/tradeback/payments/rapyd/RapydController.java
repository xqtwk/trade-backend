package grade.tradeback.payments.rapyd;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/pay")
public class RapydController {
    private final RapydService rapydService;

    public RapydController(RapydService rapydService) {
        this.rapydService = rapydService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> createCheckout(@RequestBody DepositRequest depositRequest, Principal principal) throws Exception {
        if (Objects.equals(principal.getName(), depositRequest.getUsername())) {
            System.out.println(depositRequest.getUsername());
            System.out.println(depositRequest.getCountry());
            System.out.println(depositRequest.getAmount());
            System.out.println(depositRequest.getMerchantReferenceId());
            System.out.println(depositRequest.getDescription());
            DepositResponse depositResponse = rapydService.createCheckout(
                    depositRequest.getUsername(),
                    depositRequest.getCountry(),
                    depositRequest.getAmount(),
                    depositRequest.getMerchantReferenceId(),
                    depositRequest.getDescription());
            return ResponseEntity.ok(depositResponse);
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/withdraw")
    public ResponseEntity<?> createCardPayout(@RequestBody WithdrawRequest payoutRequest, Principal principal) throws Exception {
        System.out.println(rapydService.getPayoutRequirements());
        if (Objects.equals(principal.getName(), payoutRequest.getUsername())) {
            System.out.println("got it");
            //String response = rapydService.createCardPayout(payoutRequest);
            //return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/payout/sepa")
    public ResponseEntity<String> createEuSepaBankPayout(@RequestBody SepaPayoutRequest payoutRequest) {
        try {
            String response = rapydService.createEuSepaBankPayout(payoutRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to process payout: " + e.getMessage());
        }
    }
}
