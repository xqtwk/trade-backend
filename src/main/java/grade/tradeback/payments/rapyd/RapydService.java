package grade.tradeback.payments.rapyd;

import grade.tradeback.payments.transaction.Transaction;
import grade.tradeback.payments.transaction.TransactionStatus;
import grade.tradeback.payments.transaction.TransactionType;
import grade.tradeback.payments.transaction.WebhookData;
import grade.tradeback.user.UserRepository;
import grade.tradeback.user.UserService;
import grade.tradeback.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class RapydService {
    private final String apiUrl = "https://sandboxapi.rapyd.net";
    private final String access_key = "rak_6BD2D02EF4EF6F3BE3D6";
    private final String secret_key = "rsk_48e9c9eba49031edce6688f0bdf52c16e0cb948dd25fb5304884b7a3c0010f0a15ea3ed3322bb72e";
    private final UserRepository userRepository;
    private final UserService userService;

    public RapydService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes(StandardCharsets.US_ASCII), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (InvalidKeyException e) {
            System.out.println("hmacDigest InvalidKeyException");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("hmacDigest NoSuchAlgorithmException");
        }
        return digest;
    }


    public String givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        int leftLimit = 97;   // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return (generatedString);
    }

    public String getStatus(HashMap<String, Object> responseMap) throws IOException {
        String status = "";
        if (responseMap.containsKey("status") && responseMap.get("status") instanceof HashMap) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> statusMap = (HashMap<String, Object>) responseMap.get("status");
            status = (String) statusMap.get("status");
        }
        return status;
    }

    public String getOperationId(HashMap<String, Object> responseMap) throws IOException {
        String operationId = "";
        if (responseMap.containsKey("status") && responseMap.get("status") instanceof HashMap) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> statusMap = (HashMap<String, Object>) responseMap.get("status");
            operationId = (String) statusMap.get("operation_id");
        }
        return operationId;
    }

    public String getCheckoutId(HashMap<String, Object> responseMap) throws IOException {
        String checkoutId = "";
        if (responseMap.containsKey("data") && responseMap.get("data") instanceof HashMap) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> statusMap = (HashMap<String, Object>) responseMap.get("data");
            checkoutId = (String) statusMap.get("id");
        }
        return checkoutId;
    }


    /**
     * METHODS FOR REQUESTS BELOW
     **/
    public String getCountries() throws Exception {
        String httpMethod = "get"; // Ensure this is uppercase as per your working example
        String urlPath = "/v1/data/countries";
        String salt = givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect();
        long timestamp = System.currentTimeMillis() / 1000L;
        String bodyString = ""; // Empty for GET requests

        //String queryParams = URLEncoder.encode("sender_country=LT&sender_currency=EUR&beneficiary_country=LT&payout_currency=EUR&sender_entity_type=individual&beneficiary_entity_type=individual&payout_amount=251", "UTF-8");
        String queryParams = "";
        // Concatenate the string as per your working example
        String toEnc = httpMethod + urlPath + salt + timestamp + access_key + secret_key + bodyString;
        String StrhashCode = hmacDigest(toEnc, secret_key, "HmacSHA256");
        String signature = Base64.getEncoder().encodeToString(StrhashCode.getBytes());

        // HttpClient as used in your working example
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(apiUrl + urlPath);

        // Setting the headers as per your working example
        httpget.addHeader("Content-Type", "application/json");
        httpget.addHeader("access_key", access_key);
        httpget.addHeader("salt", salt);
        httpget.addHeader("timestamp", Long.toString(timestamp));
        httpget.addHeader("signature", signature);

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        System.out.println(entity);
        return entity != null ? EntityUtils.toString(entity) : null;
    }

    public String getPayoutRequirements() throws Exception {
        String httpMethod = "get";
        String urlPath = "/v1/payout_methods/" + "eu_sepa_bank" + "/required_fields";
        String salt = givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect();
        long timestamp = System.currentTimeMillis() / 1000L;
        String bodyString = ""; // Empty for GET requests

        // Constructing query parameters
        String queryParams = "sender_country=" + URLEncoder.encode("LT", "UTF-8") +
                             "&sender_currency=" + URLEncoder.encode("EUR", "UTF-8") +
                             "&beneficiary_country=" + URLEncoder.encode("DE", "UTF-8") +
                             "&payout_currency=" + URLEncoder.encode("EUR", "UTF-8") +
                             "&sender_entity_type=" + URLEncoder.encode("company", "UTF-8") +
                             "&beneficiary_entity_type=" + URLEncoder.encode("individual", "UTF-8") +
                             "&payout_amount=" + URLEncoder.encode("251", "UTF-8");

        // Concatenating the string for signature
        String toEnc = httpMethod + urlPath + "?" + queryParams + salt + timestamp + access_key + secret_key + bodyString;
        String StrhashCode = hmacDigest(toEnc, secret_key, "HmacSHA256");
        String signature = Base64.getEncoder().encodeToString(StrhashCode.getBytes());

        // HttpClient as used in getCountries()
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(apiUrl + urlPath + "?" + queryParams);
        // Setting the headers as per your working example
        httpget.addHeader("Content-Type", "application/json");
        httpget.addHeader("access_key", access_key);
        httpget.addHeader("salt", salt);
        httpget.addHeader("timestamp", Long.toString(timestamp));
        httpget.addHeader("signature", signature);

        System.out.println(httpget);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> responseMap = objectMapper.readValue(EntityUtils.toString(entity), new TypeReference<>() {
        });

        String status = getStatus(responseMap);
        System.out.println(status);
        return status;
    }


    public String createPayout(String sender, String amount) throws Exception {
        String httpMethod = "post";
        String urlPath = "/v1/payouts";
        String salt = givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect();
        long timestamp = System.currentTimeMillis() / 1000L;

        // Construct the request body as per Rapyd API documentation example
        String requestBody = "{"
                             + "\"payout_amount\": 250,"
                             + "\"payout_currency\": \"EUR\","
                             + "\"payout_method_type\": \"eu_sepa_bank\","
                             + "\"sender_currency\": \"EUR\","
                             + "\"sender_country\": \"LT\","
                             + "\"sender_entity_type\": \"company\","
                             + "\"sender\": \"sender_1654a758e2465bcc48e26fe735a629fb\","
                             + "\"beneficiary\": \"beneficiary_f9ab0471c1272821bdabe92341e227e0\","
                             + "\"beneficiary_country\": \"LT\","
                             + "\"beneficiary_entity_type\": \"individual\","
                             + "\"description\": \"Payout - Bank Transfer: Beneficiary/Sender IDs\","
                             + "\"statement_descriptor\": \"lmao\""
                             + "}";
        requestBody = requestBody.replaceAll("\\s+", "");
        System.out.println(requestBody);
        // Generate the signature
        String toEnc = httpMethod + urlPath + salt + timestamp + access_key + secret_key + requestBody;
        String StrhashCode = hmacDigest(toEnc, secret_key, "HmacSHA256");
        String signature = Base64.getEncoder().encodeToString(StrhashCode.getBytes());

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(apiUrl + urlPath);

        // Setting the headers
        httppost.addHeader("Content-Type", "application/json");
        httppost.addHeader("access_key", access_key);
        httppost.addHeader("salt", salt);
        httppost.addHeader("timestamp", Long.toString(timestamp));
        httppost.addHeader("signature", signature);

        // Setting the request body
        httppost.setEntity(new StringEntity(requestBody));

        // Execute the request
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity) : null;
    }

    public DepositResponse createCheckout(String username, String country, int amount) throws Exception {
        if (amount == 0) {
            throw new IllegalArgumentException("Amount must not be zero");
        }
        if (amount < 0) {
            amount *= -1;
        }
        String httpMethod = "post";
        String urlPath = "/v1/checkout";
        String salt = givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect();
        long timestamp = System.currentTimeMillis() / 1000L;

        // Construct the request body for checkout creation
        String requestBody = "{"
                             + "\"amount\": 100,"
                             + "\"currency\": \"EUR\","
                             + "\"country\": \"" + country + "\","
                             + "\"complete_checkout_url\": \"https://www.rapyd.net/\","
                             + "\"cancel_checkout_url\": \"https://www.rapyd.net/\","
                             + "\"language\": \"lt\""
                             + "}";
        requestBody = requestBody.replaceAll("\\s+", "");

        // Generate the signature
        String toEnc = httpMethod + urlPath + salt + timestamp + access_key + secret_key + requestBody;
        String StrhashCode = hmacDigest(toEnc, secret_key, "HmacSHA256");
        String signature = Base64.getEncoder().encodeToString(StrhashCode.getBytes());

        // Set up HttpClient and HttpPost
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl + urlPath);

        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("access_key", access_key);
        httpPost.addHeader("salt", salt);
        httpPost.addHeader("timestamp", String.valueOf(timestamp));
        httpPost.addHeader("signature", signature);

        // Set the request body
        httpPost.setEntity(new StringEntity(requestBody));

        // Send the request and handle the response
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();


        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> responseMap = objectMapper.readValue(EntityUtils.toString(responseEntity), new TypeReference<>() {
        });
        System.out.println(responseMap);
        String checkoutId = getCheckoutId(responseMap);
        if (Objects.equals(getStatus(responseMap), "SUCCESS")) {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Transaction transaction = Transaction.builder()
                        .operationId(getOperationId(responseMap))
                        .user(user)
                        .amount(amount)
                        .status(TransactionStatus.PENDING)
                        .type(TransactionType.PAYMENT)
                        .checkoutId(checkoutId)
                        .build();
                userService.addTransactionToUser(username, transaction);
                userRepository.save(user);
            }
        }
        /*return responseEntity != null ? EntityUtils.toString(responseEntity) : null;*/
        System.out.println(getCheckoutId(responseMap));
        return DepositResponse.builder().checkoutId(checkoutId).build();
    }

    public boolean verifyWebhookSignature(String timestamp, String salt, String signature, String requestBody) {
        String urlPath = "https://pixelpact.eu/api/webhook/catch"; // Get the entire URL path

        // Recreate the string used for signature calculation
        String toEnc = urlPath + salt + timestamp + access_key + secret_key + requestBody;

        // Calculate the HMAC-SHA256 hash of the toEnc string
        String calculatedSignature = hmacDigest(toEnc, secret_key, "HmacSHA256");

        // Decode the provided signature from Base64
        //byte[] providedSignatureBytes = Base64.getDecoder().decode(signature);

        // Encode the calculated signature to Base64
        String calculatedSignatureBase64 = Base64.getEncoder().encodeToString(calculatedSignature.getBytes());

        // Compare the calculated signature (in Base64) with the provided signature (in Base64)
        System.out.println("my signature: " + calculatedSignatureBase64);
        System.out.println("signature from webhook: " + signature);
        return calculatedSignatureBase64.equals(signature);
    }
}
