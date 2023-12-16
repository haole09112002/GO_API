package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeCustomerInfoRequest {
    private String fullName;
    private Date dateOfBirth;
    private boolean gender;
    private MultipartFile avatar;
}
