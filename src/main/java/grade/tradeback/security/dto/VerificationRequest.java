package grade.tradeback.security.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = VerificationRequest.VerificationRequestBuilder.class)
public class VerificationRequest {
    private String username;
    private String code;

    @JsonPOJOBuilder(withPrefix = "")
    public static class VerificationRequestBuilder {
        @JsonProperty("username")
        private String username;

        @JsonProperty("code")
        private String code;
    }
}