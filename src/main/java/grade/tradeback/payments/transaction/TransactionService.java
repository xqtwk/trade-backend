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
    public void completePayout(Transaction transaction, double amount) {
        // Update transaction status
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

    }
    @Transactional
    public void cancelPayment(Transaction transaction) {
        // Update transaction status
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);
    }
    @Transactional
    public void cancelPayout(Transaction transaction, double amount) {
        // Update transaction status
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);
        User user = transaction.getUser();
        userService.addBalance(user.getUsername(), amount);
    }

    /*public String extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        System.out.println("body before strip: " + requestBody);
        return requestBody.toString().replaceAll("\\s+", "");
    }*/
    public String extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                //requestBody.append(line).append('\n'); // Preserve line breaks
                requestBody.append(line); // Preserve line breaks
            }
        }
        System.out.println(requestBody.toString());
        return requestBody.toString(); // Return raw JSON without altering whitespace
    }

}
