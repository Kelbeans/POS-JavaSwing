package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Dto.ApiResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiConfig {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String DISCOUNT_API_URL = "http://localhost:8084/api/v1/discount/get-discount";

    public ApiResponse callDiscountApi(String jsonRequestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DISCOUNT_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
            throw new RuntimeException("Failed to call discount API: " + e.getMessage(), e);
        }

    }
}
