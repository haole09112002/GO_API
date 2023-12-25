package com.GOBookingAPI.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.GOBookingAPI.enums.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.config.VNPayConfig;
import com.GOBookingAPI.payload.response.PaymentResponse;
import com.GOBookingAPI.services.IPaymentService;
import com.GOBookingAPI.utils.AppConstants;

import io.micrometer.common.lang.Nullable;

@RestController
@RequestMapping("/payment")
public class PaymentController {


    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private IPaymentService paymentService;


    @GetMapping("/check")
    public ResponseEntity<?> createPayment(@RequestParam Map<String, String> req) {
        System.out.println(req);
        return ResponseEntity.ok("payment");
    }

    @GetMapping("/returnUrl")
    public ResponseEntity<?> test(@RequestParam Map<String, String> req) {
        return ResponseEntity.ok("payment");
    }

    @GetMapping("/create")
    public ResponseEntity<?> create() throws UnsupportedEncodingException {
        String orderType = "other";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        long amount = 1000000L;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "goapi-production-9e3a.up.railway.app");
        vnp_Params.put("vnp_OrderType", orderType);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        cld.add(Calendar.HOUR, 7);
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);

        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return null;
    }

    @GetMapping("/link")
    public ResponseEntity<?> getPaymentLink(int bookingId, PaymentMethod paymentMethod) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(paymentService.createPaymentLink(email, bookingId, paymentMethod));
    }

    /*
        @author: HaoLV
        @description: call back from VNPay
    */
    @GetMapping("/IPN")
    public void IPNHandle(@RequestParam Map<String, String> req) {
        paymentService.handlePaymentTransaction(req);
    }

    
    @GetMapping("/statisticsdate")
    public ResponseEntity<?> getStatisticsDay(@RequestParam(name ="from" , required = false) @Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd") Date from,
    											@RequestParam(name = "to"  , required = false)@Nullable @DateTimeFormat(pattern =  "yyyy-MM-dd")  Date to,
    											@RequestParam(name ="statisticsType" , required = false) String statisticsType,
    											@RequestParam(name = "size" , required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
    											@RequestParam(name = "page" , required =  false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page
    											){
    	return ResponseEntity.ok(paymentService.getStatisticsPaymentDate(from, to,  statisticsType,  size,  page));
    }
}
