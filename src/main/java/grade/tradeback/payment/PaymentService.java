package grade.tradeback.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.TransferCreateParams;
import grade.tradeback.user.entity.User;
import grade.tradeback.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        Stripe.apiKey = apiKey;
    }

    public String createCharge(double amount, String userId, String tokenId) {
        if(tokenId == null || tokenId.isEmpty()){
            return "Invalid Token ID";
        }
        if (amount <= 0) {
            return "amount can't be <=0";
        }
        long amountInCents = (long) (amount * 100);
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amountInCents);
        chargeParams.put("currency", "eur"); // Set your currency
        chargeParams.put("description", "Top-up for user " + userId);
        chargeParams.put("source", tokenId); // Get it from request
        try {
            Charge charge = Charge.create(chargeParams);
            Optional<User> userOptional = userRepository.findById(Integer.valueOf(userId));
            if (userOptional.isEmpty()) {
                return "User not found with ID: " + userId;
            }
            User user = userOptional.get();
            user.setBalance(user.getBalance() + amount);
            userRepository.save(user);
            return charge.getId(); // Or any other detail you need
        } catch (StripeException e) {
            e.printStackTrace();
            return "Charge failed: " + e.getMessage();
        }
    }


    public Customer createStripeCustomer(String email) throws Exception {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .build();
        return Customer.create(params);
    }

    public PaymentMethod attachPaymentMethodToCustomer(String customerId, String paymentMethodId) throws Exception {
        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        return PaymentMethod.retrieve(paymentMethodId).attach(params);
    }

    public String createTransfer(Long amount, String connectedAccountId) throws StripeException {
        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amount)
                .setCurrency("eur")
                .setDestination(connectedAccountId)
                .build();

        Transfer transfer = Transfer.create(params);
        return transfer.getId();
    }
}
