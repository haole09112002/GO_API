package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentRequest {

//    private String transactionId;
//
//    private Double amount;
//
//    private Date timeStamp;
	@NotBlank
    private int bookingId;
	@NotBlank
    private String email;
	@NotBlank
    private String vnp_Amount;
	@NotBlank
    private String vnp_BankCode;
	@NotBlank
    private String vnp_BankTranNo;
	@NotBlank
    private String vnp_CardType;
	@NotBlank
    private String vnp_OrderInfo;
	@NotBlank
    private String vnp_PayDate;
	@NotBlank
    private String vnp_ResponseCode;
	@NotBlank
    private String vnp_SecureHash;
	@NotBlank
    private String vnp_TmnCode;
	@NotBlank
    private String vnp_TransactionNo;
	@NotBlank
    private String vnp_TransactionStatus;
	@NotBlank
    private String vnp_TxnRef;
}
