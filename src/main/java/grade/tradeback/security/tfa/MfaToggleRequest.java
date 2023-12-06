package grade.tradeback.security.tfa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MfaToggleRequest {
    private boolean enableMfa;
    private String otpCode;
    private String secret;
}
