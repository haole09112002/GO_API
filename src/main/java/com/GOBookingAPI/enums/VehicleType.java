package com.GOBookingAPI.enums;

import com.GOBookingAPI.exceptions.BadRequestException;

public enum VehicleType {

    MOTORCYCLE(1, 8000),
    CAR(2, 12000);

    private final int value;
    private final double price;

    VehicleType(int value, double price) {
        this.value = value;
        this.price = price;
    }

    public int getValue() {
        return value;
    }

    public double getPrice() {
        return price;
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