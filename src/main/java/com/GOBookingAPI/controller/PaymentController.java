package com.GOBookingAPI.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.config.VNPayConfig;
import com.GOBookingAPI.payload.request.PaymentRequest;
import com.GOBookingAPI.payload.response.PaymentResponse;
import com.GOBookingAPI.services.IPaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	
	@Autowired
	private IPaymentService paymentService;
	
	@PostMapping
	public ResponseEntity<?> createPayment(@RequestParam Map<String, String> req) {
		paymentService.handlePaymentTransaction(req);
        return ResponseEntity.ok("payment");
	}
//	@ModelAttribute PaymentRequest req
}
