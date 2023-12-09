package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.DriverInfoImg;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.utils.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DriverInfoResponse extends DriverBaseInfoResponse{

    private String idCard;

    private DriverStatus status ;

    private String activityArea;

    private String drivingLicense;

    private String drivingLicenseImg1;

    private String drivingLicenseImg2;

    private String cardId1;

    private String cardId2;

    public void setDriverInfoUrl(String fileName){
        this.drivingLicenseImg1 = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/" + DriverInfoImg.DrivingLicense + "_" + 0 +"_")
                .path(fileName)
                .toUriString();
        this.drivingLicenseImg2 = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/" + DriverInfoImg.DrivingLicense + "_" + 1 +"_")
                .path(fileName)
                .toUriString();
        this.cardId1 = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/" + DriverInfoImg.IdCard + "_" + 2 +"_")
                .path(fileName)
                .toUriString();
        this.cardId2 = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/" + DriverInfoImg.IdCard + "_" + 3 +"_")
                .path(fileName)
                .toUriString();
    }
}
