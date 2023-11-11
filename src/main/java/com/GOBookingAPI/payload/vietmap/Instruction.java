package com.GOBookingAPI.payload.vietmap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Instruction {

    @JsonProperty("distance")
    private double distance;

    @JsonProperty("heading")
    private int heading;

    @JsonProperty("sign")
    private int sign;

    @JsonProperty("interval")
    private List<Integer> interval;

    @JsonProperty("text")
    private String text;

    @JsonProperty("time")
    private long time;

    @JsonProperty("street_name")
    private String streetName;

    @JsonProperty("last_heading")
    private Integer lastHeading;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public List<Integer> getInterval() {
        return interval;
    }

    public void setInterval(List<Integer> interval) {
        this.interval = interval;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Integer getLastHeading() {
        return lastHeading;
    }

    public void setLastHeading(Integer lastHeading) {
        this.lastHeading = lastHeading;
    }
}