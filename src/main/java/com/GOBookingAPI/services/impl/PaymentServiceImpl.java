package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.config.VNPayConfig;
import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.PaymentMethod;
import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.PaymentRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IPaymentService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.BookingUtils;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerLocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IWebSocketService webSocketService;

    @Autowired
    private IDriverService driverService;

    @Autowired
    private ManagerLocation managerLocation;

    @Override
    @Transactional
    public void handlePaymentTransaction(Map<String, String> req) {      // callback from VNpay
        //todo valid data;
        System.out.println(req.get("email"));
        User user = userRepository.findByEmail(req.get("email")).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Booking booking = bookingRepository.findById(Integer.parseInt(req.get("bookingId"))).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + Integer.parseInt(req.get("bookingId"))));
        if (user.getId() != booking.getCustomer().getUser().getId())
            throw new AccessDeniedException("Booking không thuộc về user id: " + user.getId());
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking không ở trạng thái cần thanh toán");
        }

        // check gia tien doi chieu 
        String vnp_SecureHash = req.get("vnp_SecureHash");
        if (req.containsKey("vnp_SecureHashType")) {
            req.remove("vnp_SecureHashType");
        }
        if (req.containsKey("vnp_SecureHash")) {
            req.remove("vnp_SecureHash");
        }
        String signValue = VNPayConfig.hashAllFields(req);

        if (signValue.equals(vnp_SecureHash)) {

            boolean checkOrderId = true; // vnp_TxnRef exists in your database
            boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).
            boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)


            if (checkOrderId) {
                if (checkAmount) {
                    if (checkOrderStatus) {
                        if ("00".equals(req.get("vnp_ResponseCode"))) {
                            booking.setStatus(BookingStatus.PAID);
                            bookingRepository.save(booking);
                            Payment payment = new Payment();
                            payment.setAmount(Double.valueOf(req.get("vnp_Amount")));
                            payment.setTransactionId(req.get("vnp_TransactionNo"));
//                          payment.setTimeStamp(Date.valueOf(req.get("")));
                            payment.setCustomer(user.getCustomer());
                            payment.setBooking(booking);
                            paymentRepository.save(payment);
                            //todo sendRequestChangeBookingStatus => BookingStatus.PAID for customer
                            webSocketService.notifyBookingStatusToCustomer(user.getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
                            //todo sendRequestDriverLocation for all driver free

                            List<LocationDriver> locationDrivers = managerLocation.getByStatus(WebSocketBookingTitle.FREE.toString());
                            for (LocationDriver locaDriver : locationDrivers) {
                                webSocketService.notifytoDriver(locaDriver.getIddriver(), "HAVEBOOKING");
                            }
                            driverService.scheduleFindDriverTask(booking.getId(), booking.getPickupLocation());
                        } else {
                            throw new BadRequestException("Thanh toán không thành công !");
                        }
                        System.out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                    } else {

                        System.out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                    }
                } else {
                    System.out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                }
            } else {
                System.out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
            }
        } else {
            System.out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        }

    }

    @Override
    public String createPaymentLink(String email, int bookingId, PaymentMethod paymentMethod) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + bookingId));
        if (BookingUtils.bookingBelongToUser(booking, user)) {
            if (paymentMethod == PaymentMethod.VNPAY)
                return this.createVNPayPaymentUrl(booking);
            if (paymentMethod == PaymentMethod.MOMO) {
                // todo
                return this.createVNPayPaymentUrl(booking);
            }
        }
        throw new AccessDeniedException("Booking này không thuộc về bạn");
    }

    /*
        @author: HaoLV
        @description: create payment url for VNPay
    */
    private String createVNPayPaymentUrl(final Booking booking) {
        try {
            String orderType = "other";
            String vnp_TxnRef = String.valueOf(booking.getId());
            String amount = String.valueOf(booking.getAmount() * 100);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", amount);
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", "localhost:8080");
            vnp_Params.put("vnp_OrderType", orderType);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
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
            return paymentUrl;
        } catch (UnsupportedEncodingException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public void handlePaymentIPN(String vnp_TmnCode,
                                 String vnp_Amount,
                                 String vnp_BankCode,
                                 String vnp_BankTranNo,
                                 String vnp_CardType,
                                 String vnp_PayDate,
                                 String vnp_OrderInfo,
                                 String vnp_TransactionNo,
                                 String vnp_ResponseCode,
                                 String vnp_TransactionStatus,
                                 String vnp_TxnRef,
                                 String vnp_SecureHashType,
                                 String vnp_SecureHash) {
        Booking booking = bookingRepository.findById(Integer.parseInt(vnp_TxnRef)).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + Integer.parseInt(vnp_TxnRef)));
        //todo
        booking.setStatus(BookingStatus.PAID);
        webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
    }


}
