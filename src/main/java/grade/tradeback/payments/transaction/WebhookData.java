package grade.tradeback.payments.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // This will ignore fields that are not defined
public class WebhookData {
    private String id;
    private String type;
    private String trigger_operation_id;
}
