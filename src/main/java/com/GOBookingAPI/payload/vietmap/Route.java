package com.GOBookingAPI.payload.vietmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {

    @JsonProperty("distance")
    private double distance;

    @JsonProperty("weight")
    private double weight;

    @JsonProperty("time")
    private long time;

    @JsonProperty("points_encoded")
    private boolean pointsEncoded;

    @JsonProperty("points")
    private Points points;
}
