package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class DriverRegisterRequest {
    @NotBlank
    private String phoneNumber;

    private boolean isMale;

    @NotBlank
    private String dateOfBirth;

    @NotNull
    private MultipartFile avatar;

    @NotBlank
    private String fullName;

    @NotBlank
    private String idCard;

    @NotBlank
    private String licensePlate;

    @NotBlank
    private String drivingLicense;

    @NotNull
    private MultipartFile[] idCardImg;

    @NotNull
    private MultipartFile[] drivingLicenseImg;

    @NotNull
    private VehicleType vehicleType;

}
