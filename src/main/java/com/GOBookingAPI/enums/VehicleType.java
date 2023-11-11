package com.GOBookingAPI.enums;

public enum VehicleType {

    MOTORCYCLE(1, 8),
    CAR(2, 12);

    private final int value;
    private final double price;

    VehicleType(int value, double price) {
        this.value = value;
        this.price = price;
    }

    public int getValue() {
        return value;
    }

    public int getPrice() {
        return value;
    }

    public static VehicleType getTypeByValue(int value) {
        for (VehicleType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid VehicleType value: " + value);
    }
}