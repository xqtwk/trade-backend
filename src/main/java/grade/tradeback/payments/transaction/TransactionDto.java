package grade.tradeback.payments.transaction;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private long id;
    private String operationId;
    private TransactionType type;
    private String checkoutId;
    private double amount;
    private TransactionStatus status;
}
