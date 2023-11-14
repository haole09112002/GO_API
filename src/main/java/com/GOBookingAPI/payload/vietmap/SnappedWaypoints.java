package com.GOBookingAPI.payload.vietmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SnappedWaypoints {

    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private List<List<Double>> coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}