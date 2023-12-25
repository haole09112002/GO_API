package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.config.VNPayConfig;
import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.PaymentMethod;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.PagedResponse;
import com.GOBookingAPI.payload.response.PaymentBaseInfo;
import com.GOBookingAPI.payload.response.StatisticsPaymentBaseResponse;
import com.GOBookingAPI.payload.response.StatisticsPaymentDayOfMonth;
import com.GOBookingAPI.payload.response.StatisticsPaymentDayResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.PaymentRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentDayProjection;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentMonthProjection;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IPaymentService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
                payment.setTxnRef(vnp_TxnRef);
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
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            String vnp_RequestId = VNPayConfig.getRandomNumber(8);
            String vnp_Version = VNPayConfig.vnp_Version;
            String vnp_Command = "refund";
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
            String vnp_TransactionType = "02";
            String vnp_TxnRef = booking.getPayment().getTxnRef();        //
            long amount = booking.getAmount()*100;
            String vnp_Amount = String.valueOf(amount);
            String vnp_OrderInfo = "Hoan tien GD OrderId:" + booking.getId();
            String vnp_TransactionNo = ""; //Assuming value of the parameter "vnp_TransactionNo" does not exist on your system.
            String vnp_CreateBy = "Go_API";

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            String vnp_TransactionDate = formatter.format(cld.getTime());
            String vnp_IpAddr = "127.0.0.1";

            JsonObject vnp_Params = new JsonObject ();

            vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
            vnp_Params.addProperty("vnp_Version", vnp_Version);
            vnp_Params.addProperty("vnp_Command", vnp_Command);
            vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
            vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.addProperty("vnp_Amount", vnp_Amount);
            vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

            if(vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty())
            {
                vnp_Params.addProperty("vnp_TransactionNo", "{get value of vnp_TransactionNo}");
            }

            vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
            vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
            vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

            String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                    vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                    vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data.toString());

            vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

            try {
                URL url = new URL (VNPayConfig.vnp_ApiUrl);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(vnp_Params.toString());
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                System.out.println("nSending 'POST' request to URL : " + url);
                System.out.println("Post Data : " + vnp_Params);
                System.out.println("Response Code : " + responseCode);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String output;
                StringBuffer response = new StringBuffer();
                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                in.close();
                System.out.println(response.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(response.toString(), new TypeReference<Map<String, Object>>() {});
                String vnp_ResponseCode = (String) responseMap.get("vnp_ResponseCode");
                if(vnp_ResponseCode.equals("00")){
                    System.out.println("============> REFUND: " + booking.getId());
                    booking.setStatus(BookingStatus.REFUNDED);
                    bookingRepository.save(booking);
                    isSuccess.set(true);
                    webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));   //
                    executorService.shutdown();
                }
            }catch (Exception e){
                System.out.println("Exception in payment service, repeat again");
            }
        }, AppConstants.INIT_DELAY, AppConstants.PERIOD_TIME, TimeUnit.SECONDS);
        return isSuccess.get();
    }

	@Override
	public StatisticsPaymentBaseResponse getStatisticsPaymentDate(Date from, Date to , String statisticsType, int size, int page) {
		List<StatisticsPaymentDayResponse> statisticsPaymentDayResponses = new ArrayList<StatisticsPaymentDayResponse>();
		int totalAmount=0 ;
		int number =0;
		double avg =0;  
		int totalResuls =0;
		if(statisticsType == null) {
			statisticsType = "day";
		}
		switch (statisticsType) {
		case "month": {
			if(from == null || to == null) {
				Calendar calendar1 = Calendar.getInstance();
				calendar1.add(Calendar.MONTH, 1);
				calendar1.add(Calendar.YEAR, -1);
				from = calendar1.getTime();
				
				Calendar calendar2 = Calendar.getInstance();
				calendar2.add(Calendar.MONTH, 12);
				calendar2.add(Calendar.YEAR, -1);
				to = calendar2.getTime();
				
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int monthFrom = calFrom.get(Calendar.MONTH) + 1;
			
			Calendar calTo= Calendar.getInstance();
			calTo.setTime(to);
			int monthTo = calTo.get(Calendar.MONTH) +1;
			
			int yearFrom = calFrom.get(Calendar.YEAR);
			int yearTo = calFrom.get(Calendar.YEAR);
			log.info("from {} {} to {} {}" ,monthFrom, yearFrom , monthTo ,yearTo);
			List<StatisticsPaymentMonthProjection> StatisticsMonth = paymentRepository.getStatisticsMonth(monthFrom, monthTo,yearFrom ,yearTo);
			List<StatisticsPaymentDayProjection> StatisticsDayOfMonth = paymentRepository.getInforMaxDayOfMonth(monthFrom, monthTo, yearFrom ,yearTo);
			number = paymentRepository.getCountTransaction(monthFrom, monthTo);
			for(int i = 0 ; i< StatisticsMonth.size() ; i++) {
				totalAmount +=StatisticsMonth.get(i).getTotal(); 
				statisticsPaymentDayResponses.add(new StatisticsPaymentDayResponse(StatisticsMonth.get(i).getMonth(),
																					StatisticsMonth.get(i).getTotal(),
																					new StatisticsPaymentDayOfMonth(
																							StatisticsDayOfMonth.get(i).getDate(),
																							StatisticsDayOfMonth.get(i).getTotal()
																							)));
			}
			totalResuls =StatisticsMonth.size();
			try {
				avg = totalAmount/number;
			} catch (Exception e) {
				avg = 0;
			}
		
			break;
		}
		
		case "day" :{
			if(from == null || to == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -7);
				from = calendar.getTime();
				to = new Date();
			}
			List<StatisticsPaymentDayProjection> projection = paymentRepository.getStatisticsDay(from, to);
			List<Payment> payments = paymentRepository.getInforPaymentMaxDay(from, to);
			number = paymentRepository.getCountTransaction(from, to);
			for(int i = 0 ; i< projection.size() ; i++) {
				totalAmount+= projection.get(i).getTotal();
				
				statisticsPaymentDayResponses.add(new StatisticsPaymentDayResponse(projection.get(i).getDate(),
																					projection.get(i).getTotal(),
																					new PaymentBaseInfo(
																							payments.get(i).getId(),
																							payments.get(i).getTransactionId(),
																							payments.get(i).getAmount(),
																							payments.get(i).getTimeStamp(),
																							payments.get(i).getPaymentMethod())
																					));
			}
			totalResuls = projection.size();
			try {
				avg = totalAmount/number;
			} catch (Exception e) {
				avg = 0;
			}
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		PageRequest pageRequest = PageRequest.of(size, page);
		Page<StatisticsPaymentDayResponse> pagedResponse = new PageImpl<>(statisticsPaymentDayResponses, pageRequest, totalResuls);
		PagedResponse<StatisticsPaymentDayResponse> StatisticsPaymentDayPagedResponse = new PagedResponse<StatisticsPaymentDayResponse>(pagedResponse.getContent(),pagedResponse.getNumber(),pagedResponse.getSize(),
																										   pagedResponse.getTotalElements(),pagedResponse.getTotalPages(),pagedResponse.isLast());
		return new StatisticsPaymentBaseResponse(totalAmount, number, avg, StatisticsPaymentDayPagedResponse);
	}
    
    
    


}
