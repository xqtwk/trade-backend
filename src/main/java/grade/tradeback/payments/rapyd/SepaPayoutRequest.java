package grade.tradeback.payments.rapyd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SepaPayoutRequest {
    private String senderCurrency = "EUR";
    private String senderCountry;
    private String senderEntityType = "company";
    private String beneficiaryCountry;
    private String payoutCurrency = "EUR";
    private String beneficiaryEntityType = "individual";

    // Beneficiary details
    private String beneficiaryFirstName;
    private String beneficiaryLastName;
    private String beneficiaryIban;

    // Sender details
    private String senderCompanyName;

    private int amount;
    private String description;
    private String statementDescriptor;
}
