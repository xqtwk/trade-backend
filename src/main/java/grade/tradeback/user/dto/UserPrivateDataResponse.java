package grade.tradeback.user.dto;

import grade.tradeback.payments.transaction.TransactionDto;
import grade.tradeback.user.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivateDataResponse {
    private long id;
    private String username;
    private String email;
    private Role role;
    private double balance;
    private boolean mfaEnabled;
    private List<TransactionDto> transactions;
}
