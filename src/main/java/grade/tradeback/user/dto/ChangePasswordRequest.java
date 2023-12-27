package grade.tradeback.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
    private final String currentPassword;
    private final String newPassword;
    private final String confirmationPassword;

    @JsonCreator
    public ChangePasswordRequest(@JsonProperty("currentPassword") String currentPassword,
                                 @JsonProperty("newPassword") String newPassword,
                                 @JsonProperty("confirmationPassword") String confirmationPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmationPassword = confirmationPassword;
    }
}
