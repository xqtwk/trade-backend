package grade.tradeback.payment.plaid;

import com.plaid.client.model.ItemPublicTokenExchangeResponse;
import com.plaid.client.model.LinkTokenCreateResponse;
import com.plaid.client.model.LinkTokenGetResponse;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/plaid")
public class PlaidController {
    private final PlaidService plaidService;
    private final UserRepository userRepository;

    public PlaidController(PlaidService plaidService,
                           UserRepository userRepository) {
        this.plaidService = plaidService;
        this.userRepository = userRepository;
    }

    @PostMapping("/exchange_public_token")
    public ResponseEntity<?> exchangePublicToken(@RequestBody ExchangePublicTokenRequest request) {
        try {
            ItemPublicTokenExchangeResponse response = plaidService.exchangePublicToken(request.getPublicToken(), request.getUsername());
            HashMap<String, String> tokenDetails = new HashMap<>();
            tokenDetails.put("access_token", response.getAccessToken());
            tokenDetails.put("item_id", response.getItemId());
            return ResponseEntity.ok(tokenDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in exchanging public token: " + e.getMessage());
        }
    }

    @PostMapping("/create_link_token") // PUBLIC TOKEN
    public ResponseEntity<?> createLinkToken(@RequestBody LinkTokenRequestByUsername request) {
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            String userId = String.valueOf(user.getId());
            System.out.println(userId);
            LinkTokenCreateResponse response = plaidService.createLinkToken(userId);
            return ResponseEntity.ok(response.getLinkToken());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating link token: " + e.getMessage());
        }
    }

    @PostMapping("/get_token")
    public LinkTokenGetResponse getLinkToken(@RequestBody LinkTokenRequest tokenRequest) throws Exception {
        return plaidService.getLinkToken(tokenRequest);
    }


}
