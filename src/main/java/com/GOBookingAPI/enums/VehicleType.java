package com.GOBookingAPI.enums;

import com.GOBookingAPI.exceptions.BadRequestException;

public enum VehicleType {

    MOTORCYCLE(1, 6000, 0.75),
    CAR(2, 8000, 0.62);

    private final int value;
    private final double price;
    private final double percent;

    VehicleType(int value, double price, double percent) {
        this.value = value;
        this.price = price;
        this.percent = percent;
    }

    public int getValue() {
        return value;
    }

    public double getPrice() {
        return price;
    }

    public double getPercent() {
        return percent;
    }

    public static VehicleType getTypeByValue(int value) {
        for (VehicleType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new BadRequestException("Invalid VehicleType value: " + value);
    }
}