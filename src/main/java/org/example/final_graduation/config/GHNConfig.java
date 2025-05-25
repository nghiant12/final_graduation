package org.example.final_graduation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GHNConfig {
    @Value("${ghn.api.token}")
    private String token;

    @Value("${ghn.shop.id}")
    private String shopId;

    public static final String BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    private static final String CALCULATE_FEE_URL = BASE_URL + "/v2/shipping-order/fee";
    private static final String GET_PROVINCES_URL = BASE_URL + "/master-data/province";
    private static final String GET_DISTRICTS_URL = BASE_URL + "/master-data/district";
    private static final String GET_WARDS_URL = BASE_URL + "/master-data/ward";

    public String getToken() {
        return token;
    }

    public String getShopId() {
        return shopId;
    }

    public String getCalculateFeeUrl() {
        return CALCULATE_FEE_URL;
    }

    public String getProvincesUrl() {
        return GET_PROVINCES_URL;
    }

    public String getDistrictsUrl() {
        return GET_DISTRICTS_URL;
    }

    public String getWardsUrl() {
        return GET_WARDS_URL;
    }

    public static final String GHN_API_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data";
} 