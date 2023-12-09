package grade.tradeback.payment.checkoutcom;

import com.checkout.CheckoutApi;
import com.checkout.CheckoutApiException;
import com.checkout.CheckoutArgumentException;
import com.checkout.CheckoutAuthorizationException;

import com.checkout.accounts.Individual;
import com.checkout.accounts.OnboardEntityRequest;
import com.checkout.accounts.Profile;
import com.checkout.common.*;
import com.checkout.payments.*;
import com.checkout.payments.links.PaymentLinkRequest;
import com.checkout.payments.links.PaymentLinkResponse;
import com.checkout.payments.request.PaymentInstruction;
import com.checkout.payments.request.PaymentRequest;
import com.checkout.payments.request.PayoutRequest;
import com.checkout.payments.request.destination.PaymentRequestBankAccountDestination;
import com.checkout.payments.request.destination.PaymentRequestCardDestination;
import com.checkout.payments.request.source.*;
import com.checkout.payments.response.AuthorizationResponse;
import com.checkout.payments.response.PaymentResponse;
import com.checkout.payments.response.PayoutResponse;
import com.checkout.payments.sender.PaymentIndividualSender;
import com.checkout.payments.request.AuthorizationRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CardPayoutService {

    @Value("${application.checkout.processing_channel_id}")
    private String processingChannelId;

    @Value("${application.checkout.secret_key}")
    private String checkout_secretKey;
    private final CheckoutApi checkoutApi;

    public CardPayoutService(CheckoutApi checkoutApi) {
        this.checkoutApi = checkoutApi;
    }

    public PaymentStatus generatePaymentLink(String destinationCardId, long amount) throws ExecutionException, InterruptedException {
        PaymentLinkRequest paymentLinksRequest = PaymentLinkRequest.builder()
                .amount(10L)
                .currency(Currency.GBP)
                .reference("reference")
                .description("description")
                .expiresIn(604800)
                .billing(BillingInformation.builder()
                        .address(Address.builder()
                                .country(CountryCode.LT)
                                .build())
                        .build())
                .processingChannelId(processingChannelId)
                .build();

        try {
            PaymentLinkResponse response = checkoutApi.paymentLinksClient().createPaymentLink(paymentLinksRequest).get();
            return PaymentStatus.AUTHORIZED;
        } catch (CheckoutApiException e) {
            // API error
            String requestId = e.getRequestId();
            int statusCode = e.getHttpStatusCode();
            Map<String, Object> errorDetails = e.getErrorDetails();
        } catch (CheckoutArgumentException e) {
            // Bad arguments
        } catch (CheckoutAuthorizationException e) {
            // Invalid authorization
        }
        return PaymentStatus.DECLINED;
    }

    public PaymentStatus requestCardPayment() {
        RequestCardSource source = RequestCardSource.builder()
                .number("4242424242424242")
                .expiryMonth(10)
                .expiryYear(2027)
                .cvv("100")
                .stored(false)
                .build();

        PaymentIndividualSender sender = PaymentIndividualSender.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .address(Address.builder()
                        .addressLine1("123 High St.")
                        .addressLine2("Flat 456")
                        .city("London")
                        .state("GB")
                        .zip("SW1A 1AA")
                        .country(CountryCode.GB)
                        .build())
                .identification(AccountHolderIdentification.builder()
                        .type(AccountHolderIdentificationType.DRIVING_LICENCE)
                        .number("1234")
                        .issuingCountry(CountryCode.GB)
                        .build())
                .build();

        ThreeDSRequest threeDSRequest = ThreeDSRequest.builder()
                .enabled(true)
                .challengeIndicator(ChallengeIndicator.NO_CHALLENGE_REQUESTED)
                .build();

        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .amount(10000L)
                .reference("reference")
                .build();

        PaymentRequest request = PaymentRequest.builder()
                .source(source)
                .sender(sender)
                .capture(false)
                .reference("reference")
                .amount(10000L)
                .currency(Currency.EUR)
                //.threeDS(threeDSRequest)
                .successUrl("https://docs.checkout.com/success")
                .failureUrl("https://docs.checkout.com/failure")
                .processingChannelId(processingChannelId)
                .build();

        try {
            PaymentResponse response = checkoutApi.paymentsClient().requestPayment(request).get();
           // AuthorizationResponse authResponse = checkoutApi.paymentsClient().incrementPaymentAuthorization(response.getId(), authorizationRequest).get();
            return PaymentStatus.AUTHORIZED;
            //return authResponse.getStatus();
        } catch (CheckoutApiException e) {
            // ... (exception handling)
        } catch (CheckoutArgumentException e) {
            // ... (exception handling)
        } catch (CheckoutAuthorizationException e) {
            // ... (exception handling)
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return PaymentStatus.DECLINED;
    }

    public PaymentStatus requestPayout() {
        //checkoutApi.accountsClient().createEntity(OnboardEntityRequest.builder().individual(Individual.builder().build()).build());
        PayoutRequestEntitySource psource = PayoutRequestEntitySource.builder().id("ca_pt5kvurrlmrube2crzaqdhqbdm").amount(1000L).build();
        PayoutRequestCurrencyAccountSource pss = PayoutRequestCurrencyAccountSource.builder().id("ca_pt5kvurrlmrube2crzaqdhqbdm").build();
        RequestCardSource source = RequestCardSource.builder()
                .number("123456789")
                .expiryMonth(10)
                .expiryYear(2028)
                .cvv("123")
                .stored(false)
                .build();
        PayoutRequest payoutRequest = PayoutRequest.builder()
                .destination(PaymentRequestBankAccountDestination.builder()
                        .country(CountryCode.LT)
                        .accountHolder(AccountHolder.builder()
                                .firstName("Jonas")
                                .lastName("Jonavicius")
                                .billingAddress(Address.builder()
                                        .country(CountryCode.LT)
                                        .build())
                                .build())
                        .iban("LT121000011101001000")
                        .build()
                )
                .source(pss)
                .amount(1000L)
                .currency(Currency.EUR)
                .instruction(PaymentInstruction.builder().fundsTransferType("withdrawal").build())
                .reference("payout pixelpact")
                .processingChannelId(processingChannelId)
                .build();

        try {
            PayoutResponse response = checkoutApi.paymentsClient().requestPayout(payoutRequest).get();
            return PaymentStatus.AUTHORIZED;
        } catch (CheckoutApiException e) {
            // ... (exception handling)
        } catch (CheckoutArgumentException e) {
            // ... (exception handling)
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return PaymentStatus.DECLINED;
    }



    private String getPaymentContextId() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();

        String payload = String.join("\n"
                , "{"
                , " \"source\": {"
                , "  \"type\": \"paypal\""
                , " },"
                , " \"amount\": 6540,"
                , " \"currency\": \"USD\","
                , " \"payment_type\": \"Recurring\","
                , " \"capture\": true,"
                , " \"shipping\": {"
                , "  \"first_name\": \"John\","
                , "  \"last_name\": \"Smith\","
                , "  \"email\": \"john.smith@example.com\","
                , "  \"address\": {"
                , "   \"address_line1\": \"123 High St.\","
                , "   \"address_line2\": \"Flat 456\","
                , "   \"city\": \"London\","
                , "   \"state\": \"str\","
                , "   \"zip\": \"SW1A 1AA\","
                , "   \"country\": \"GB\""
                , "  },"
                , "  \"phone\": {"
                , "   \"country_code\": \"+1\","
                , "   \"number\": \"415 555 2671\""
                , "  },"
                , "  \"from_address_zip\": \"123456\","
                , "  \"timeframe\": \"SameDay\","
                , "  \"method\": \"BillingAddress\","
                , "  \"delay\": 5"
                , " },"
                , " \"processing\": {"
                , "  \"plan\": {"
                , "   \"type\": \"MERCHANT_INITIATED_BILLING\","
                , "   \"skip_shipping_address\": true,"
                , "   \"immutable_shipping_address\": true"
                , "  },"
                , "  \"shipping_amount\": 300,"
                , "  \"invoice_id\": \"string\","
                , "  \"brand_name\": \"Acme Corporation\","
                , "  \"locale\": \"en-US\","
                , "  \"shipping_preference\": \"set_provided_address\","
                , "  \"user_action\": \"pay_now\","
                , "  \"partner_customer_risk_data\": {"
                , "   \"key\": \"string\","
                , "   \"value\": \"string\""
                , "  },"
                , "  \"airline_data\": ["
                , "   {"
                , "    \"ticket\": {"
                , "     \"number\": \"045-21351455613\","
                , "     \"issue_date\": \"2023-05-20\","
                , "     \"issuing_carrier_code\": \"AI\","
                , "     \"travel_package_indicator\": \"B\","
                , "     \"travel_agency_name\": \"World Tours\","
                , "     \"travel_agency_code\": \"01\""
                , "    },"
                , "    \"passenger\": ["
                , "     {"
                , "      \"first_name\": \"John\","
                , "      \"last_name\": \"White\","
                , "      \"date_of_birth\": \"1990-05-26\","
                , "      \"address\": {"
                , "       \"country\": \"US\""
                , "      }"
                , "     }"
                , "    ],"
                , "    \"flight_leg_details\": ["
                , "     {"
                , "      \"flight_number\": \"101\","
                , "      \"carrier_code\": \"BA\","
                , "      \"class_of_travelling\": \"J\","
                , "      \"departure_airport\": \"LHR\","
                , "      \"departure_date\": \"2023-06-19\","
                , "      \"departure_time\": \"15:30\","
                , "      \"arrival_airport\": \"LAX\","
                , "      \"stop_over_code\": \"x\","
                , "      \"fare_basis_code\": \"SPRSVR\""
                , "     }"
                , "    ]"
                , "   }"
                , "  ]"
                , " },"
                , " \"processing_channel_id\": \"pc_q4dbxom5jbgudnjzjpz7j2z6uq\","
                , " \"reference\": \"ORD-5023-4E89\","
                , " \"description\": \"Set of 3 masks\","
                , " \"success_url\": \"https://example.com/payments/success\","
                , " \"failure_url\": \"https://example.com/payments/fail\","
                , " \"items\": ["
                , "  {"
                , "   \"name\": \"Test item\","
                , "   \"quantity\": 2,"
                , "   \"unit_price\": 50,"
                , "   \"reference\": \"858818ac\","
                , "   \"total_amount\": 29000,"
                , "   \"tax_amount\": 1000,"
                , "   \"discount_amount\": 1000,"
                , "   \"url\": \"string\","
                , "   \"image_url\": \"string\""
                , "  }"
                , " ]"
                , "}"
        );// JSON payload as per Checkout.com example

                HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.sandbox.checkout.com/payment-contexts"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + checkout_secretKey) // Replace with your actual token
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response to extract the payment_context_id
        // This is a simplified example. You'll need to parse the JSON response properly.
        return extractPaymentContextId(response.body());
    }
    private String extractPaymentContextId(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.optString("payment_context_id", ""); // Replace "payment_context_id" with the actual key name
    }
}
