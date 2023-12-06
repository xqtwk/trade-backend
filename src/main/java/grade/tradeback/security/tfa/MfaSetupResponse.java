package grade.tradeback.security.tfa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MfaSetupResponse {
    private String qrCodeImageUri;
    private String newSecret;
}
