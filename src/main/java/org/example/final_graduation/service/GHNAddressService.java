package org.example.final_graduation.service;

import org.example.final_graduation.config.GHNConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GHNAddressService {
    private final RestTemplate restTemplate;
    private final GHNConfig ghnConfig;

    @Autowired
    public GHNAddressService(GHNConfig ghnConfig) {
        this.restTemplate = new RestTemplate();
        this.ghnConfig = ghnConfig;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token", ghnConfig.getToken());
        return headers;
    }

    public ResponseEntity<?> getProvinces() {
        String url = GHNConfig.GHN_API_URL + "/province";
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.get("code").equals(200)) {
                return ResponseEntity.ok(responseBody.get("data"));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch provinces");
    }

    public ResponseEntity<?> getDistricts(Integer provinceId) {
        String url = GHNConfig.GHN_API_URL + "/district";
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("province_id", provinceId);

        HttpEntity<?> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.get("code").equals(200)) {
                return ResponseEntity.ok(responseBody.get("data"));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch districts");
    }

    public ResponseEntity<?> getWards(Integer districtId) {
        String url = GHNConfig.GHN_API_URL + "/ward?district_id=" + districtId;
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.get("code").equals(200)) {
                return ResponseEntity.ok(responseBody.get("data"));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch wards");
    }

    public Integer calculateShippingFee(Integer districtId, Integer wardCode, Integer weight) {
        String url = GHNConfig.BASE_URL;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("from_district_id", 1454); // Shop's district ID
        requestBody.put("from_ward_code", "21211"); // Shop's ward code
        requestBody.put("service_type_id", 2); // Standard delivery service
        requestBody.put("to_district_id", districtId);
        requestBody.put("to_ward_code", String.valueOf(wardCode)); // Convert to string as required by API
        requestBody.put("Weight", 1); // Capitalized as required by API
        requestBody.put("Insurance_Value", 10000); // Default insurance value
        requestBody.put("Height", 20);
        requestBody.put("Length", 20);
        requestBody.put("Width", 20);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, createHeaders());

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.get("code").equals(200)) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    return ((Number) data.get("total")).intValue();
                }
            }
            return 30000; // Default shipping fee if calculation fails
        } catch (Exception e) {
            e.printStackTrace();
            return 30000; // Default shipping fee in case of error
        }
    }
} 