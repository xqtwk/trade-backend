package grade.tradeback.payments.rapyd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest {
    private String username;
    private double amount;
    private String senderCurrency;
    private String senderCountry;
    private Beneficiary beneficiary;
    private String beneficiaryCountry;
    private String payoutCurrency;
    private String senderEntityType;
    private String beneficiaryEntityType;
    private Sender sender;
    private String description;
    private String statementDescriptor;
    private String cardNumber;
    private String cardExpirationMonth;
    private String cardExpirationYear;
    private String cardCvv;
    private String firstName;
    private String lastName;
    // Getters and setters
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Beneficiary {
        private String email;
        private String cardNumber;
        private String cardExpirationMonth;
        private String cardExpirationYear;
        private String cardCvv;
        private String firstName;
        private String lastName;

        // Getters and setters
    }

}
