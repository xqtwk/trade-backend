package grade.tradeback.payments.transaction;

import grade.tradeback.payments.rapyd.RapydService;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
import grade.tradeback.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;

@CrossOrigin(origins = "*", allowCredentials = "false")
@Controller
@RequestMapping("/webhook")
public class TransactionController {
    private final RapydService rapydService;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(RapydService rapydService, TransactionRepository transactionRepository, TransactionService transactionService, UserRepository userRepository) {
        this.rapydService = rapydService;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/catch")
    public void catchWebhook(
            HttpServletRequest request,
            @RequestHeader("timestamp") String timestamp,
            @RequestHeader("salt") String salt,
            @RequestHeader("signature") String signature
    ) throws Exception{
        System.out.println("webhook catched: ");
        System.out.println("webhook signature: " + signature);
        System.out.println("webhook salt: " + salt);
        System.out.println("timestamp: " + timestamp);
        String requestBody = transactionService.extractRequestBody(request);
        System.out.println("webhook requestbody: " + requestBody);

        ObjectMapper objectMapper = new ObjectMapper();

        WebhookData webhookData = objectMapper.readValue(requestBody, WebhookData.class);
        String description = webhookData.getData().getDescription();
        double amount = webhookData.getData().getAmount();

        if (rapydService.verifyWebhookSignature(timestamp, salt, signature, requestBody)) {
            System.out.println("signature matches");
            System.out.println("User Nickname from Description: " + description);
            User user = userRepository.findByUsername(description)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            // Use the User entity and amount to find the transaction
            Transaction transaction = transactionRepository.findByUserAndAmount(
                            user,
                            amount)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
            if (Objects.equals(webhookData.getType(), "PAYMENT_COMPLETED") && transaction.getType().equals(TransactionType.PAYMENT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.completePaymentAndUpdateBalance(transaction, transaction.getAmount());
                }
            }
            if ((Objects.equals(webhookData.getType(), "PAYMENT_CANCELED")
                 || Objects.equals(webhookData.getType(), "PAYMENT_EXPIRED")
                || Objects.equals(webhookData.getType(), "PAYMENT_FAILED")) && transaction.getType().equals(TransactionType.PAYMENT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.cancelPayment(transaction);
                }
            }
            if (Objects.equals(webhookData.getType(), "PAYOUT_COMPLETED") && transaction.getType().equals(TransactionType.PAYOUT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.completePayout(transaction, transaction.getAmount());
                }
            }
            if ((Objects.equals(webhookData.getType(), "PAYOUT_CANCELED")
                 || Objects.equals(webhookData.getType(), "PAYOUT_EXPIRED")
                 || Objects.equals(webhookData.getType(), "PAYOUT_FAILED")) && transaction.getType().equals(TransactionType.PAYOUT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.cancelPayout(transaction, amount);
                }
            }
        } else {
            throw new IllegalArgumentException("Wrong webhook's signature");
        }


    }

}
