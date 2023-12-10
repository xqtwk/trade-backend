package grade.tradeback.payments.transaction;

import grade.tradeback.payments.rapyd.RapydService;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

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
            @RequestBody WebhookData webhookData,
            @RequestHeader("timestamp") String timestamp,
            @RequestHeader("salt") String salt,
            @RequestHeader("signature") String signature
    ) {

        if (rapydService.verifyWebhookSignature(timestamp, salt, signature, webhookData)) {
            Transaction transaction = transactionRepository.findByOperationId(webhookData.getTrigger_operation_id())
                    .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
            if (Objects.equals(webhookData.getType(), "PAYMENT_COMPLETED") && transaction.getType().equals(TransactionType.PAYMENT)) {
                if (transaction.getStatus().equals(TransactionStatus.PENDING)) {
                    transactionService.completePaymentAndUpdateBalance(transaction, transaction.getAmount());
                }
            }
        } else {
            throw new IllegalArgumentException("Wrong webhook's signature");
        }

    }

}
