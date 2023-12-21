package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public interface MapService {

    VietMapResponse getRoute(String from, String to, String motorcycle);

    String convertLocationToAddress(String location);
}
