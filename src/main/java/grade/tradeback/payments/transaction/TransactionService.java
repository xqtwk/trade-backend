package grade.tradeback.payments.transaction;

import grade.tradeback.user.UserService;
import grade.tradeback.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
