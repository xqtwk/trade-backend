package grade.tradeback.payments.rapyd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sender {
    private String companyName;
    private String postcode;
    private String city;
    private String state;
    private String address;

}
