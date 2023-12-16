package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.config.VNPayConfig;
import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.PaymentMethod;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.PaymentRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IPaymentService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
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

    @Autowired
    private DriverRepository driverRepository;

    @Override
    @Transactional
    public void handlePaymentTransaction(Map<String, String> req) {      // callback from VNpay
        String vnp_TmnCode = req.get("vnp_TmnCode");
        String vnp_Amount = req.get("vnp_Amount");
        String vnp_BankCode = req.get("vnp_BankCode");
        String vnp_BankTranNo = req.get("vnp_BankTranNo");
        String vnp_CardType = req.get("vnp_CardType");
        String vnp_PayDate = req.get("vnp_PayDate");
        String vnp_OrderInfo = req.get("vnp_OrderInfo");
        String vnp_TransactionNo = req.get("vnp_TransactionNo");
        String vnp_ResponseCode = req.get("vnp_ResponseCode");
        String vnp_TransactionStatus = req.get("vnp_TransactionStatus");
        String vnp_TxnRef = req.get("vnp_TxnRef");

        String vnp_SecureHashType = req.get("vnp_SecureHashType");
        String vnp_SecureHash = req.get("vnp_SecureHash");

        for(String s : req.values())
            System.out.print(s + " ");


        if (req.containsKey("vnp_SecureHashType")) {
            req.remove("vnp_SecureHashType");
        }
        if (req.containsKey("vnp_SecureHash")) {
            req.remove("vnp_SecureHash");
        }

        int bookingId = VNPayConfig.getBookingIdByTxnRef(vnp_TxnRef);
        if (bookingId == -1)
            return;

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + bookingId));

        String signValue = VNPayConfig.hashAllFields(req);
        if (true) {
            if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                System.out.println("==>Verify FAIL, bookingId: " + bookingId +", Booking not status: WAITING, bookingId: " + bookingId);
                return;
            }

            long vnpAmount = Long.parseLong(vnp_Amount) / 100;
            if (vnpAmount != booking.getAmount()) {
                System.out.println("==>Verify FAIL, bookingId: " + bookingId +", booking amount not equals vnpAmount: " + booking.getAmount() + ", " + vnpAmount);
                return;
            }

            if (vnp_TransactionStatus.equals("00")) {
                booking.setStatus(BookingStatus.PAID);
                bookingRepository.save(booking);

                Payment payment = new Payment();
                payment.setId(bookingId);
                payment.setTransactionId(vnp_TransactionNo);
                payment.setAmount(vnpAmount);
                payment.setCustomer(booking.getCustomer());
                payment.setBooking(booking);
                payment.setTimeStamp(AppUtils.convertTimeStringVNPayToDate(vnp_PayDate));
                payment.setPaymentMethod(PaymentMethod.VNPAY);
                paymentRepository.save(payment);

                //todo sendRequestDriverLocation for all driver free

                List<Driver> drivers = driverRepository.findDriverStatus(DriverStatus.FREE);

//                List<LocationDriver> locationDrivers =  managerLocation.getLocationMapFree().values().stream().toList();
                if(drivers.isEmpty())
                    System.out.println("drivers.isEmpty()");

                for (Driver d : drivers) {
                    webSocketService.notifytoDriver(d.getId(), "HAVEBOOKING");
                }
                driverService.scheduleFindDriverTask(booking, booking.getPickUpLocation());
            }
        } else {
            System.out.println("==>Verify FAIL, bookingId: " + bookingId +", invalid checksum ");
        }

        System.out.println("Payment process success and send to customer, bookingId: " + booking.getId() + ", " + booking.getStatus().name());
        webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
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
            String amount = String.valueOf(booking.getAmount() * 100);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", amount);
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB");

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
            String vnp_TxnRef = vnp_CreateDate + booking.getId();
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);

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

    @Transactional
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

//        int bookingId = VNPayConfig.getBookingIdByTxnRef(vnp_TxnRef);
//        boolean isSuccess = true;
//        if (bookingId == -1) {
//            System.out.println("=> invalid vnp_TxnRef, " + vnp_TxnRef);
//            return;
//        }
//
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + Integer.parseInt(vnp_TxnRef)));
//
//        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
//            System.out.println("=> Booking status is not valid, bookingStatus" + booking.getStatus().name());
//            isSuccess = false;
//            return;
//        }
//
//        if (vnp_TransactionStatus.equals("00")) {
//            long vnpAmount = Long.parseLong(vnp_Amount) / 100;
//            if (vnpAmount == booking.getAmount()) {
//                booking.setStatus(BookingStatus.PAID);
//                bookingRepository.save(booking);
//
//                Payment payment = new Payment();
//                payment.setTransactionId(vnp_TransactionNo);
//                payment.setAmount(vnpAmount);
//                payment.setCustomer(booking.getCustomer());
//                payment.setBooking(booking);
//                payment.setTimeStamp(AppUtils.convertTimeStringVNPayToDate(vnp_PayDate));
//                paymentRepository.save(payment);
//
//                //todo sendRequestDriverLocation for all driver free
//                List<LocationDriver> locationDrivers = managerLocation.getByStatus(WebSocketBookingTitle.FREE.toString());
//                for (LocationDriver localDriver : locationDrivers) {
//                    webSocketService.notifytoDriver(localDriver.getIddriver(), "HAVEBOOKING");
//                }
//                driverService.scheduleFindDriverTask(booking.getId(), booking.getPickupLocation());
//            } else
//                log.info("Fail payment, booking amount not equals vnpAmount: " + booking.getAmount() + ", " + vnpAmount);
//        } else {
//            log.info("Fail payment, bookingId: " + booking.getId());
//        }
//        log.info("Payment process success and send to customer, bookingId: " + booking.getId());
//        webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
    }

    @Override
    public boolean refundPayment(Booking booking){
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            //todo fix refunded
            System.out.println("============> REFUND: " + booking.getId());
            booking.setStatus(BookingStatus.REFUNDED);
            bookingRepository.save(booking);
            isSuccess.set(false);
            webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));   //
            executorService.shutdown();
        }, AppConstants.INIT_DELAY, AppConstants.PERIOD_TIME, TimeUnit.SECONDS);
        return isSuccess.get();
    }

}
