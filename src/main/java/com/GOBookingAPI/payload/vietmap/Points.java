package com.GOBookingAPI.payload.vietmap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Points {

    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private List<List<Double>> coordinates;
}