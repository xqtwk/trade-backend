package grade.tradeback.payments.transaction;

import grade.tradeback.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOperationId(String operationId);
    Optional<Transaction> findByUserAndAmount(User user, double amount);


}
