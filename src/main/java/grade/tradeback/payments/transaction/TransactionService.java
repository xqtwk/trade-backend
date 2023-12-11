package grade.tradeback.payments.transaction;

import grade.tradeback.user.UserService;
import grade.tradeback.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    @Transactional
    public void completePaymentAndUpdateBalance(Transaction transaction, double amount) {
        // Update transaction status
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        // Update user balance
        User user = transaction.getUser();
        userService.addBalance(user.getUsername(), amount);
    }
    @Transactional
    public void cancelPayment(Transaction transaction) {
        // Update transaction status
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);
    }

    public String extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString().replaceAll("\\s+", "");
    }

}
