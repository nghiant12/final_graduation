package org.example.final_graduation.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.final_graduation.config.GHNConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GHNService {
    @Autowired
    private GHNConfig ghnConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, Object>> getProvinces() {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                ghnConfig.getProvincesUrl(),
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        return objectMapper.convertValue(
                response.getBody().get("data"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );
    }

    public List<Map<String, Object>> getDistricts(Integer provinceId) {
        HttpHeaders headers = createHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("province_id", provinceId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                ghnConfig.getDistrictsUrl(),
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        return objectMapper.convertValue(
                response.getBody().get("data"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );
    }

    public List<Map<String, Object>> getWards(Integer districtId) {
        HttpHeaders headers = createHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("district_id", districtId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                ghnConfig.getWardsUrl(),
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        return objectMapper.convertValue(
                response.getBody().get("data"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );
    }

    public Integer calculateShippingFee(Integer toDistrictId, Integer toWardCode, Integer weight) {
        HttpHeaders headers = createHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("service_type_id", 2); // Chuẩn
        body.put("insurance_value", 0);
        body.put("to_ward_code", toWardCode.toString());
        body.put("to_district_id", toDistrictId);
        body.put("from_district_id", 1454); // Quận/Huyện của shop
        body.put("weight", weight);
        body.put("length", 15);
        body.put("width", 15);
        body.put("height", 15);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                ghnConfig.getCalculateFeeUrl(),
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        return response.getBody().get("data").get("total").asInt();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", ghnConfig.getToken());
        headers.set("ShopId", ghnConfig.getShopId());
        return headers;
    }
} 