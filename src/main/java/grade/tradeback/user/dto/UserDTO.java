package grade.tradeback.user.dto;

import grade.tradeback.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String username;
    private String email;
    private Role role;
    private double balance;
    private boolean mfaEnabled;
}
