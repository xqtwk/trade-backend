package grade.tradeback.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import grade.tradeback.payment.plaid.PlaidService;
import grade.tradeback.user.entity.User;
import grade.tradeback.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StripeService {
    @Value("${application.stripe.api.key}")
    private String apiKey;

    private final UserRepository userRepository;
    private final PlaidService plaidService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }


    public StripeService(UserRepository userRepository, PlaidService plaidService) {
        this.userRepository = userRepository;
        this.plaidService = plaidService;
        Stripe.apiKey = apiKey;
    }

    public String createCharge(double amount, String userId, String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
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



    public Account addExternalBankAccount(String accountId, String externalAccountToken) throws StripeException {
        Account account = Account.retrieve(accountId);

        AccountUpdateParams params = AccountUpdateParams.builder()
                .setExternalAccount(externalAccountToken) // Token representing the external account
                .build();

        return account.update(params);
    }

    public Account createCustomAccountWithDetails(CustomAccountRequest accountRequest) throws StripeException {
        AccountCreateParams params = AccountCreateParams.builder()
                .setCountry(accountRequest.getCountry())
                .setEmail(accountRequest.getEmail())
                .setType(AccountCreateParams.Type.CUSTOM)
                .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                .setBusinessProfile(new AccountCreateParams.BusinessProfile.Builder()
                        .setUrl(accountRequest.getUrl())
                        .build())
                .setTosAcceptance(new AccountCreateParams.TosAcceptance.Builder()
                        .setIp(accountRequest.getTosIp())
                        .setDate(accountRequest.getTosDate())
                        .build())
                .setIndividual(new AccountCreateParams.Individual.Builder()
                        .setFirstName(accountRequest.getFirstName())
                        .setLastName(accountRequest.getLastName())
                        .setDob(new AccountCreateParams.Individual.Dob.Builder()
                                .setDay(accountRequest.getDobDay())
                                .setMonth(accountRequest.getDobMonth())
                                .setYear(accountRequest.getDobYear())
                                .build())
                        .setAddress(new AccountCreateParams.Individual.Address.Builder()
                                .setLine1(accountRequest.getLine1())
                                .setPostalCode(accountRequest.getPostalCode())
                                .setCity(accountRequest.getCity())
                                .build())
                        .build())
                // Note: 'external_account' is typically added in a separate process
                .build();

        return Account.create(params);
    }

    public PaymentMethod attachPaymentMethodToAccount(String accountId, String paymentMethodId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(accountId)
                .build();

        return paymentMethod.attach(attachParams);
    }


}
