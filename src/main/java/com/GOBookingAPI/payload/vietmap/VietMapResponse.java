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
public class VietMapResponse {

    @JsonProperty("license")
    private String license;

    @JsonProperty("code")
    private String code;

    @JsonProperty("messages")
    private String messages;

    @JsonProperty("paths")
    private List<Route> routes;

    public Route getFirstPath(){
        return this.routes.get(0);
    }
}
