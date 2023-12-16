package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.exceptions.BadRequestException;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeCustomerInfoRequest {

    @Nullable
    private String fullName;

    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @Nullable
    private Boolean gender;

    @Nullable
    private MultipartFile avatar;

    public boolean isNull() {
        return (this.fullName == null || this.fullName.isBlank()) && this.getGender() == null && this.getDateOfBirth() == null && this.avatar == null;
    }
}
