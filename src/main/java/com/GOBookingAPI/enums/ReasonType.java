package com.GOBookingAPI.enums;

public enum ReasonType {
    DRIVER(0.5),
    CUSTOMER(0);

    private final double value;

    ReasonType(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
