package grade.tradeback.payments.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookData {
    private String timestamp;
    private String salt;
    private String signature;


    private String id;
    private String type;
    private String trigger_operation_id;
}
