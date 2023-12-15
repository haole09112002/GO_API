package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MapServiceImpl {
    private String vietmapApiUrl = "https://maps.vietmap.vn/api";

    @Value("${vietmap.api.key}")
    private String vietmapApiKey;

    private RestTemplate restTemplate;

    @Autowired
    public MapServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MapServiceImpl() {
    }

    public VietMapResponse getRoute(String from, String to, String motorcycle) {
        String apiUrl = vietmapApiUrl + "/route?api-version=1.1&apiKey=" + vietmapApiKey
                + "&point=" + from
                + "&point=" + to
                + "&vehicle=" + motorcycle + "&optimize=true&points_encoded=false";

        // Gửi yêu cầu GET đến API và nhận phản hồi dưới dạng String
        String json = restTemplate.getForObject(apiUrl, String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            VietMapResponse vietMapResponse = objectMapper.readValue(json, VietMapResponse.class);
            return vietMapResponse;
        } catch (JsonProcessingException e) {
            log.error("Error convert json to class VietMapResponse", e);
            return null;
        }
    }

    public String convertLocationToAddress(String location) {
        try {
            String[] coordinates = location.split(",");
            String lng = coordinates[1].trim();
            String lat = coordinates[0].trim();
            String apiUrl = String.format("%s/reverse/v3?apikey=%s&lng=%s&lat=%s", vietmapApiUrl, vietmapApiKey, lng, lat);
            System.out.println("===> " + apiUrl);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String json = responseEntity.getBody();

                JsonParser jsonParser = new JsonParser();
                JsonElement rootElement = jsonParser.parse(json);

                if (rootElement.isJsonObject()) {
                    JsonObject jsonObject = rootElement.getAsJsonObject();

                    if (jsonObject.has("address")) {
                        return jsonObject.get("address").getAsString();
                    } else {
                        System.out.println("Không có thông tin địa chỉ trong phản hồi JSON.");
                        return null;
                    }
                } else {
                    System.out.println("Phản hồi không phải là JSON Object.");
                    return null;
                }
            } else {
                System.out.println("Yêu cầu không thành công. Mã trạng thái: " + responseEntity.getStatusCodeValue());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi chuyển đổi vị trí thành địa chỉ: " + e.getMessage());
            return null;
        }
    }
}
