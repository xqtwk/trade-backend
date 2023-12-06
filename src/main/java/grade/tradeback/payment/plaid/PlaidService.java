package grade.tradeback.payment.plaid;

import com.plaid.client.model.*;
import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PlaidService {
    @Value("${application.plaid.api.client_id}")
    private String client_id;
    @Value("${application.plaid.api.secret}")
    private String secret;
    @Value("${application.plaid.api.url}")
    private String apiEnv;
    private final UserRepository userRepository;

    public LinkTokenCreateResponse createLinkToken(String clientId) throws IOException {
        HashMap<String, String> apiKeys = new HashMap<String, String>() {{
            put("clientId", client_id);
            put("secret", secret);
        }};
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(apiEnv); // ApiClient.Development for development, ApiClient.Production for production
        PlaidApi plaidClient = apiClient.createService(PlaidApi.class);
        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser().clientUserId(clientId);
        LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .user(user.clientUserId(clientId))
                .clientName("Pixelpact")
                .products(List.of(Products.AUTH))
                .countryCodes(List.of(
                        CountryCode.LT, // Lithuania
                        CountryCode.GB, // United Kingdom
                        CountryCode.ES, // Spain
                        CountryCode.NL, // Netherlands
                        CountryCode.FR, // France
                        CountryCode.IE, // Ireland
                        CountryCode.DE, // Germany
                        CountryCode.IT, // Italy
                        CountryCode.PL, // Poland
                        CountryCode.DK, // Denmark
                        CountryCode.NO, // Norway
                        CountryCode.SE, // Sweden
                        CountryCode.EE, // Estonia
                        CountryCode.LV, // Latvia
                        CountryCode.PT, // Portugal
                        CountryCode.BE  // Belgium
                ))
                .language("lt");
        Response<LinkTokenCreateResponse> response = plaidClient.linkTokenCreate(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            System.out.println(response.body());
            return response.body();
        } else {
            String errorDetails = response.errorBody() != null ? response.errorBody().string() : "No error details";
            throw new IOException("Failed to create link token. Response: " + errorDetails);
        }
    }
    public ItemPublicTokenExchangeResponse exchangePublicToken(String publicToken, String username) throws IOException {
        HashMap<String, String> apiKeys = new HashMap<String, String>() {{
            put("clientId", client_id);
            put("secret", secret);
        }};
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(apiEnv); // ApiClient.Development for development, ApiClient.Production for production
        PlaidApi plaidClient = apiClient.createService(PlaidApi.class);

        ItemPublicTokenExchangeRequest exchangeRequest = new ItemPublicTokenExchangeRequest().publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> response = plaidClient.itemPublicTokenExchange(exchangeRequest).execute();
        if (response.isSuccessful()) {
            ItemPublicTokenExchangeResponse responseBody = response.body();
            if (responseBody != null) {
                User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
                user.setPlaid_AccessToken(responseBody.getAccessToken());
                user.setPlaid_item_id(responseBody.getItemId());
                userRepository.save(user);
            }
        }
        System.out.println(response.body());
        return response.body();
    }

    public LinkTokenGetResponse getLinkToken(LinkTokenRequest tokenRequest) throws Exception {
        HashMap<String, String> apiKeys = new HashMap<>();
        if (tokenRequest.getClientId() != null && tokenRequest.getSecret() != null) {
            apiKeys.put("clientId", tokenRequest.getClientId());
            apiKeys.put("secret", tokenRequest.getSecret());
        } else {
            throw new IllegalArgumentException("Client ID or Secret is null");
        }

        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(apiEnv);
        PlaidApi plaidClient = apiClient.createService(PlaidApi.class);

        LinkTokenGetRequest request = new LinkTokenGetRequest().linkToken(tokenRequest.getLinkToken());

        try {
            Response<LinkTokenGetResponse> response = plaidClient.linkTokenGet(request).execute();
            if (!response.isSuccessful()) {
                String errorDetails = response.errorBody() != null ? response.errorBody().string() : "No error details";
                throw new Exception("Failed to get link token. Response: " + errorDetails);
            }
            return response.body();
        } catch (IOException e) {
            throw new Exception("IO Exception in getting link token", e);
        }
    }
}
