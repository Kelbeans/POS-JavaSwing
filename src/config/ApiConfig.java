package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.dto.ApiResponse;
import common.components.Modal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiConfig {
    private static final HttpClient client = HttpClient.newHttpClient();
    // private static final String BASE_DISCOUNT_API_URL = "http://ds-elb-8084-223253500.ap-southeast-1.elb.amazonaws.com:8084/api/v1/discount"; // AWS ELB URL PORT 8084
    private static final String BASE_DISCOUNT_API_URL = "http://ds-lb-default-port-421948242.ap-southeast-1.elb.amazonaws.com/api/v1/discount"; // AWS ELB URL PORT 8084
    private Modal popUpModal;

    public ApiResponse callDiscountApi(String jsonRequestBody, String discountType, String couponCode) {
        try {
            // Build URL with query parameters
            StringBuilder urlBuilder = new StringBuilder(BASE_DISCOUNT_API_URL);
            urlBuilder.append("?discountType=").append(discountType);

            // Add coupon code as query parameter if it's a COUPON type
            if ("COUPON".equals(discountType) && couponCode != null && !couponCode.trim().isEmpty()) {
                urlBuilder.append("&couponCode=").append(couponCode);
            }

            String fullUrl = urlBuilder.toString();
            System.out.println("Sending request to API URL: " + fullUrl);
            System.out.println("Request body: " + jsonRequestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("API Response Status: " + response.statusCode());
            System.out.println("API Response Body: " + response.body());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();

                // Parse the full response and extract just the "object" part
                JsonNode rootNode = mapper.readTree(response.body());
                JsonNode objectNode = rootNode.get("object");

                // Convert the object node to your DTO
                ApiResponse apiResponse = mapper.treeToValue(objectNode, ApiResponse.class);

                System.out.println("Discount API Response: " + apiResponse);

                return apiResponse;
            } else {
                throw new RuntimeException("API call failed with status: " + response.statusCode() +
                        ", Response: " + response.body());
            }
        } catch (Exception e) {
            popUpModal = new Modal();
            popUpModal.displayModal("Internal Server Error",
                    "An internal server error occurred. Please try again later.",
                    "OK", "Cancel");
            throw new RuntimeException("Failed to call discount API: " + e.getMessage(), e);
        }
    }

    // Overloaded method for backward compatibility (if needed)
    public ApiResponse callDiscountApi(String jsonRequestBody) {
        // Default to no discount type - you might want to handle this differently
        return callDiscountApi(jsonRequestBody, "NONE", null);
    }


}
