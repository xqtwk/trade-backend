package grade.tradeback.payments;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Payout;
import grade.tradeback.user.User;
import grade.tradeback.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class PaymentService {
    @Value("${application.stripe.api.key}")
    private String apiKey;

    private final UserRepository userRepository;
    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }


    public PaymentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createCharge(double amount, String userId, String tokenId) {
        if(tokenId == null || tokenId.isEmpty()){
            return "Invalid Token ID";
        }
        long amountInCents = (long) (amount * 100);
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amountInCents);
        chargeParams.put("currency", "eur"); // Set your currency
        chargeParams.put("description", "Top-up for user " + userId);
        chargeParams.put("source", tokenId); // Get it from request
        try {
            Charge charge = Charge.create(chargeParams);
            User user = userRepository.findById(Integer.valueOf(userId)).orElseThrow();
            user.setBalance(user.getBalance() + amount);
            userRepository.save(user);
            return charge.getId(); // Or any other detail you need
        } catch (StripeException e) {
            e.printStackTrace();
            return "Charge failed: " + e.getMessage();
        }
    }

    public String payout(double amount, String userId) {
        // Convert amount to smalest currency unit, e.g., cents for USD
        long amountInCents = (long) (amount * 100);
        Map<String, Object> payoutParams = new HashMap<>();
        payoutParams.put("amount", amountInCents);
        payoutParams.put("currency", "eur"); // Set your currency

        try {
            Payout payout = Payout.create(payoutParams);
            User user = userRepository.findById(Integer.valueOf(userId)).orElseThrow();
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);
            return payout.getId(); // Or any other detail you need
        } catch (StripeException e) {
            return "Something's wrong: " + e.getMessage();
        }
    }
}
