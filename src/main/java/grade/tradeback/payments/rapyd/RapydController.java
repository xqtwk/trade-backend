package grade.tradeback.payments.rapyd;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pay")
public class RapydController {
    private final RapydService rapydService;

    public RapydController(RapydService rapydService) {
        this.rapydService = rapydService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> createCheckout(@RequestBody DepositRequest depositRequest) throws Exception {
        System.out.println(depositRequest.getUsername());
        System.out.println(depositRequest.getCountry());
        System.out.println(depositRequest.getAmount());
        DepositResponse depositResponse = rapydService.createCheckout(
                depositRequest.getUsername(),
                depositRequest.getCountry(),
                depositRequest.getAmount());
        return ResponseEntity.ok(depositResponse);
    }
}
