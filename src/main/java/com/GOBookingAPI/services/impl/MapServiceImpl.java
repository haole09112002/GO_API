package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MapServiceImpl {
    private String vietmapApiUrl = "https://maps.vietmap.vn/api/route?api-version=1.1";

    @Value("${vietmap.api.key}")
    private String vietmapApiKey;

    private  RestTemplate restTemplate;

    @Autowired
    public MapServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MapServiceImpl() {
    }

    public VietMapResponse getRoute(String from, String to, String motorcycle){
        String apiUrl = vietmapApiUrl + "&apiKey=" + vietmapApiKey
               + "&point=" + from
                +"&point=" + to
                +"&vehicle=" + motorcycle+ "&optimize=true&points_encoded=false";

        // Gửi yêu cầu GET đến API và nhận phản hồi dưới dạng String
        String json = restTemplate.getForObject(apiUrl, String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            VietMapResponse vietMapResponse = objectMapper.readValue(json, VietMapResponse.class);
            return vietMapResponse;
        }catch (JsonProcessingException e){
            log.error("Error convert json to class VietMapResponse", e);
            return null;
        }


        // Xử lý dữ liệu kết quả nếu cần
//        System.out.println(result);
    }
}