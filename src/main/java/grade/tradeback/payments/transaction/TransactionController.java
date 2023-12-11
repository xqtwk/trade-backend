package grade.tradeback.payments.transaction;

import grade.tradeback.payments.rapyd.RapydService;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
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

    public TransactionController(RapydService rapydService, TransactionRepository transactionRepository, TransactionService transactionService) {
        this.rapydService = rapydService;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @PostMapping("/catch")
    public void catchWebhook(
            HttpServletRequest request,
            @RequestHeader("timestamp") String timestamp,
            @RequestHeader("salt") String salt,
            @RequestHeader("signature") String signature
    ) throws Exception{
        System.out.println("webhook catched");
        String requestBody = transactionService.extractRequestBody(request);

        ObjectMapper objectMapper = new ObjectMapper();

        WebhookData webhookData = objectMapper.readValue(requestBody, WebhookData.class);


        if (rapydService.verifyWebhookSignature(timestamp, salt, signature, requestBody)) {
            System.out.println("signature matches");
            Transaction transaction = transactionRepository.findByOperationId(webhookData.getTrigger_operation_id())
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
                    transactionService.completePaymentAndUpdateBalance(transaction, transaction.getAmount());
                }
            }
            if (Objects.equals(webhookData.getType(), "PAYOUT_COMPLETED") && transaction.getType().equals(TransactionType.PAYOUT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.completePaymentAndUpdateBalance(transaction, transaction.getAmount());
                }
            }
            if ((Objects.equals(webhookData.getType(), "PAYOUT_CANCELED")
                 || Objects.equals(webhookData.getType(), "PAYOUT_EXPIRED")
                 || Objects.equals(webhookData.getType(), "PAYUOT_FAILED")) && transaction.getType().equals(TransactionType.PAYOUT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.cancelPayment(transaction);
                }
            }
        } else {
            throw new IllegalArgumentException("Wrong webhook's signature");
        }


    }

}
