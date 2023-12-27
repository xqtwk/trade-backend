package grade.tradeback.payments.transaction;

import grade.tradeback.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private long id;

    private String operationId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
