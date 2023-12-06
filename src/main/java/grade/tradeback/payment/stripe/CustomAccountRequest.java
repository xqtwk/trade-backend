package grade.tradeback.payment.stripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomAccountRequest {
    private String country;
    private String email;
    private String url; // Business URL
    private String tosIp; // IP address for TOS acceptance
    private long tosDate; // Timestamp for TOS acceptance
    // Individual information
    private String firstName;
    private String lastName;
    private long dobDay;
    private long dobMonth;
    private long dobYear;
    private String line1; // Address line
    private String postalCode;
    private String city;
    private String iban;
}
