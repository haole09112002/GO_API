package com.GOBookingAPI.payload.request;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

	private MultipartFile avatar;
	
	@NotBlank(message = "phoneNumber cannot be blank")
	@Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại không hợp lệ. Vui lòng kiểm tra lại.")
	private String phoneNumber;
	
	private String dateOfBirth;
	
	private String fullName;
	
	private Boolean gender;
	
	
}
