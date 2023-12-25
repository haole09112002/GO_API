package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.exceptions.AppException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.services.MapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
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
public class MapServiceImpl implements MapService {
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
            return objectMapper.readValue(json, VietMapResponse.class);
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

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String json = responseEntity.getBody();

                JsonParser jsonParser = new JsonParser();
                JsonElement rootElement = jsonParser.parse(json);

                if (rootElement.isJsonArray()) {
                    JsonArray jsonArray = rootElement.getAsJsonArray();

                    if (jsonArray.size() > 0) {
                        JsonObject firstResult = jsonArray.get(0).getAsJsonObject();

                        if (firstResult.has("display")) {
                            return firstResult.get("display").getAsString();
                        } else {
                            System.err.println("Không có thông tin địa chỉ trong phản hồi JSON.");
                            return null;
                        }
                    } else {
                        System.err.println("Danh sách kết quả trống.");
                        return null;
                    }
                } else {
                    System.err.println("Phản hồi không phải là JSON Array.");
                    return null;
                }
            } else {
                System.err.println("Yêu cầu không thành công. Mã trạng thái: " + responseEntity.getStatusCodeValue());
                throw new AppException("Yêu cầu không thành công. Mã trạng thái: " + responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            System.err.println("Lỗi chuyển đổi vị trí thành địa chỉ: " + e.getMessage());
            throw new BadRequestException("Lỗi chuyển đổi vị trí thành địa chỉ: " + e.getMessage());
        }
    }
}
