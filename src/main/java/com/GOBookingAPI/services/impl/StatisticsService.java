package com.GOBookingAPI.services.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.response.BaseBookingResponse;
import com.GOBookingAPI.payload.response.BasePaymentResponse;
import com.GOBookingAPI.payload.response.BaseReviewResponse;
import com.GOBookingAPI.payload.response.PaymentBaseInfo;
import com.GOBookingAPI.payload.response.StatisticsBookingResponse;
import com.GOBookingAPI.payload.response.StatisticsPaymentResponse;
import com.GOBookingAPI.payload.response.StatisticsResponse;
import com.GOBookingAPI.payload.response.StatisticsReviewResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.PaymentRepository;
import com.GOBookingAPI.repositories.ReviewRepository;
import com.GOBookingAPI.repositories.projection.StatisticsBookingCountProjections;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentDayProjection;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentMonthProjection;
import com.GOBookingAPI.repositories.projection.StatisticsReviewBaseProjection;
import com.GOBookingAPI.services.IStatisticsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatisticsService implements IStatisticsService{

	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Override
	public StatisticsResponse getStatisticsResponse(Date from, Date to, String unit) {
		return new StatisticsResponse(getStatisticsPayment(from,to,unit),getStatisticsBooking(from, to, unit),getStatisticsReview(from, to, unit));
	}

	
	private StatisticsPaymentResponse getStatisticsPayment(Date from, Date to, String unit) {
		Map<String , BasePaymentResponse> mapResponse = new HashMap<String, BasePaymentResponse>();
		Long amount= 0L ;
		int total = 0;
		double average =0;  
		if(unit == null) {
			unit = "date";
		}
		switch (unit) {
		
		case "annually" :{
			if(from == null ) {
				Calendar calendar1 = Calendar.getInstance();
				calendar1.add(Calendar.YEAR, 0);
				from = calendar1.getTime();
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int year = calFrom.get(Calendar.YEAR) ;
			
			List<StatisticsPaymentMonthProjection> projection = paymentRepository.getStatisticsMonthOfYear(year);
			for(int i = 0 ; i< projection.size() ; i++) {
				
				amount += projection.get(i).getAmount();
				total += projection.get(i).getTotal();
				mapResponse.put(String.valueOf(projection.get(i).getMonth()), new BasePaymentResponse(projection.get(i).getAmount(), projection.get(i).getTotal()));
			}
			try {
				average = amount/12;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		
		
		case "monthly": {
			if(from == null ) {
				Calendar calendar1 = Calendar.getInstance();
				from = calendar1.getTime();
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int month = calFrom.get(Calendar.MONTH) + 1;
			int year = calFrom.get(Calendar.YEAR) ;
			int daysInMonth = calFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
			List<StatisticsPaymentDayProjection> projection = paymentRepository.getStatisticsDateofMonth(month,year);
			for(int i = 0 ; i< projection.size() ; i++) {
				
			
			
				amount += projection.get(i).getAmount();
				total += projection.get(i).getTotal();
				mapResponse.put(projection.get(i).getDate().toString(), new BasePaymentResponse(projection.get(i).getAmount(), projection.get(i).getTotal()));
			}
			try {
				average = amount/daysInMonth;
			} catch (Exception e) {
				average = 0;
			}
		
			break;
		}
		
		case "date" :{
			if(from == null || to == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -30);
				from = calendar.getTime();
				to = new Date();
			}
			LocalDate localStart = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate localEnd = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long distanceDate = ChronoUnit.DAYS.between(localStart, localEnd);
			if(distanceDate > 30) {
				Calendar calendar  = Calendar.getInstance();
				calendar.setTime(from);
				calendar.add(Calendar.DATE, 30);
				to = calendar.getTime();
			}
			List<StatisticsPaymentDayProjection> projection = paymentRepository.getStatisticsDate(from, to);
			
			for(int i = 0 ; i< projection.size() ; i++) {
				amount += projection.get(i).getAmount();
				total += projection.get(i).getTotal();
				mapResponse.put(projection.get(i).getDate().toString(), new BasePaymentResponse(projection.get(i).getAmount(), projection.get(i).getTotal()));
			}
			
			try {
				average = amount/distanceDate;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		
		return new StatisticsPaymentResponse(amount, average, total, mapResponse);
	}
	
	private StatisticsBookingResponse getStatisticsBooking(Date from, Date to, String unit) {
		Map<String , BaseBookingResponse> mapResponse = new HashMap<String, BaseBookingResponse>();
		int total = 0;
		int success = 0;
		int cancelled = 0;
		double average =0;  
		if(unit == null) {
			unit = "date";
		}
		switch (unit) {
		case "annually" :{
			if(from == null ) {
				Calendar calendar1 = Calendar.getInstance();
				calendar1.add(Calendar.YEAR, 0);
				from = calendar1.getTime();
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int year = calFrom.get(Calendar.YEAR) ;
			log.info("test {}",year);
			List<StatisticsBookingCountProjections> countComplete = bookingRepository.getCountByStatusMonthOfYear( year,BookingStatus.COMPLETE.toString());
			List<StatisticsBookingCountProjections> countCancel= bookingRepository.getCountByStatusMonthOfYear(year,BookingStatus.CANCELLED.toString());
			List<StatisticsBookingCountProjections> count = bookingRepository.getCountByStatusMonthOfYear( year,"");
			List<StatisticsBookingCountProjections> countRefunded= bookingRepository.getCountByStatusMonthOfYear(year,BookingStatus.REFUNDED.toString());
			
			for(int i = 0 ; i< count.size() ; i++) {
				log.info("test {}",count.get(i).getDay().toString());
				total += count.get(i).getCount();
				int datesucess = 0;
				int datecancle = 0;
				int daterefunded = 0;
				
				for(int j = 0 ; j < countComplete.size(); j++) {
					if(count.get(i).getDay()== countComplete.get(j).getDay()) {
						datesucess =countComplete.get(j).getCount();
						success +=datesucess;
						break;
					}
				}
				for(int j = 0 ; j < countCancel.size(); j++) {
					if(count.get(i).getDay()== countCancel.get(j).getDay()) {
						datecancle =countCancel.get(j).getCount();
						cancelled +=datecancle;
						break;
					}
				}
				for(int j = 0 ; j < countRefunded.size(); j++) {
					if(count.get(i).getDay()== countRefunded.get(j).getDay()) {
						daterefunded =countRefunded.get(j).getCount();
						cancelled +=daterefunded;
						break;
					}
				}
				mapResponse.put(count.get(i).getDay().toString(), new BaseBookingResponse(count.get(i).getCount(),datesucess, datecancle+daterefunded));
			}
			
			try {
				average = total/12;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		
		
		case "monthly": {
			if(from == null ) {
				Calendar calendar1 = Calendar.getInstance();
				from = calendar1.getTime();
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int month = calFrom.get(Calendar.MONTH) + 1;
			int year = calFrom.get(Calendar.YEAR) ;
			int daysInMonth = calFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
			List<StatisticsBookingCountProjections> countComplete = bookingRepository.getCountByStatusDateOfMonth(month, year,BookingStatus.COMPLETE.toString());
			List<StatisticsBookingCountProjections> countCancel= bookingRepository.getCountByStatusDateOfMonth(month, year,BookingStatus.CANCELLED.toString());
			List<StatisticsBookingCountProjections> count = bookingRepository.getCountByStatusDateOfMonth(month, year,"");
			List<StatisticsBookingCountProjections> countRefunded = bookingRepository.getCountByStatusDateOfMonth(month, year,BookingStatus.REFUNDED.toString());
			
			for(int i = 0 ; i< count.size() ; i++) {
				total += count.get(i).getCount();
				int datesucess = 0;
				int datecancle = 0;
				int daterefunded = 0;
				for(int j = 0 ; j < countComplete.size(); j++) {
					if(count.get(i).getDay().equals(countComplete.get(j).getDay())) {
						datesucess =countComplete.get(j).getCount();
						success +=datesucess;
						break;
					}
				}
				for(int j = 0 ; j < countCancel.size(); j++) {
					if(count.get(i).getDay().equals(countCancel.get(j).getDay())) {
						datecancle =countCancel.get(j).getCount();
						cancelled +=datecancle;
						break;
					}
				}
				for(int j = 0 ; j < countRefunded.size(); j++) {
					if(count.get(i).getDay().equals(countRefunded.get(j).getDay())) {
						daterefunded =countRefunded.get(j).getCount();
						cancelled +=daterefunded;
						break;
					}
				}
				mapResponse.put(count.get(i).getDay().toString(), new BaseBookingResponse(count.get(i).getCount(),datesucess, datecancle+daterefunded));
			}
			
			try {
				average = total/daysInMonth;
			} catch (Exception e) {
				average = 0;
			}
		
			break;
		}
		
		case "date" :{
			if(from == null || to == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -30);
				from = calendar.getTime();
				to = new Date();
			}
			LocalDate localStart = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate localEnd = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long distanceDate = ChronoUnit.DAYS.between(localStart, localEnd);
			if(distanceDate > 30) {
				Calendar calendar  = Calendar.getInstance();
				calendar.setTime(from);
				calendar.add(Calendar.DATE, 30);
				to = calendar.getTime();
			}
			List<StatisticsBookingCountProjections> countComplete = bookingRepository.getCountByStatusDate(from, to,BookingStatus.COMPLETE.toString());
			List<StatisticsBookingCountProjections> countCancel= bookingRepository.getCountByStatusDate(from, to,BookingStatus.CANCELLED.toString());
			List<StatisticsBookingCountProjections> count = bookingRepository.getCountByStatusDate(from, to,"");
			List<StatisticsBookingCountProjections> countRefunded = bookingRepository.getCountByStatusDate(from,to,BookingStatus.REFUNDED.toString());
			
			for(int i = 0 ; i< count.size() ; i++) {
				total += count.get(i).getCount();
				int datesucess = 0;
				int datecancle = 0;
				int daterefunded = 0;
				for(int j = 0 ; j < countComplete.size(); j++) {
					if(count.get(i).getDay().equals(countComplete.get(j).getDay())) {
						datesucess =countComplete.get(j).getCount();
						success +=datesucess;
						break;
					}
				}
				for(int j = 0 ; j < countCancel.size(); j++) {
					if(count.get(i).getDay().equals(countCancel.get(j).getDay())) {
						datecancle =countCancel.get(j).getCount();
						cancelled +=datecancle;
						break;
					}
				}
				for(int j = 0 ; j < countRefunded.size(); j++) {
					if(count.get(i).getDay().equals(countRefunded.get(j).getDay())) {
						daterefunded =countRefunded.get(j).getCount();
						cancelled +=daterefunded;
						break;
					}
				}
				
				mapResponse.put(count.get(i).getDay().toString(), new BaseBookingResponse(count.get(i).getCount(),datesucess, datecancle+daterefunded));
			}
			
			try {
				average = total/distanceDate;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		
		return new StatisticsBookingResponse(total, average, success,cancelled, mapResponse);
	}
	
	private StatisticsReviewResponse getStatisticsReview(Date from, Date to, String unit) {
		Map<String , BaseReviewResponse> mapResponse = new HashMap<String, BaseReviewResponse>();
		int total = 0;
		double average =0;  
		if(unit == null) {
			unit = "date";
		}
		switch (unit) {
		case "annually" :{
			if(from == null ) {
				Calendar calendar1 = Calendar.getInstance();
				calendar1.add(Calendar.YEAR, 0);
				from = calendar1.getTime();
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int year = calFrom.get(Calendar.YEAR) ;
			
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewMonth(year);
			for(int i = 0 ; i< projection.size() ; i++) {
				total+= projection.get(i).getCount();
				mapResponse.put(projection.get(i).getDate().toString(), new BaseReviewResponse(projection.get(i).getFiveStar(),
																							   projection.get(i).getFourStar(),
																							   projection.get(i).getThreeStar(),
																							   projection.get(i).getTwoStar(),
																							   projection.get(i).getOneStar()));
			}
			try {
				average = total/12 ;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		
		
		case "monthly": {
			if(from == null ) {
				Calendar calendar1 = Calendar.getInstance();
				from = calendar1.getTime();
			}
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(from);
			int month = calFrom.get(Calendar.MONTH) + 1;
			int year = calFrom.get(Calendar.YEAR) ;
			int daysInMonth = calFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewDateOfMonth(month, year);
			for(int i = 0 ; i< projection.size() ; i++) {
				total+= projection.get(i).getCount();
				mapResponse.put(projection.get(i).getDate().toString(), new BaseReviewResponse(projection.get(i).getFiveStar(),
																							   projection.get(i).getFourStar(),
																							   projection.get(i).getThreeStar(),
																							   projection.get(i).getTwoStar(),
																							   projection.get(i).getOneStar()));
			}
			try {
				average = total/daysInMonth;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		
		case "date" :{
			if(from == null || to == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -30);
				from = calendar.getTime();
				to = new Date();
			}
			LocalDate localStart = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate localEnd = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long distanceDate = ChronoUnit.DAYS.between(localStart, localEnd);
			if(distanceDate > 30) {
				Calendar calendar  = Calendar.getInstance();
				calendar.setTime(from);
				calendar.add(Calendar.DATE, 30);
				to = calendar.getTime();
			}
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewDate(from, to);
			for(int i = 0 ; i< projection.size() ; i++) {
				total+= projection.get(i).getCount();
				mapResponse.put(projection.get(i).getDate().toString(), new BaseReviewResponse(projection.get(i).getFiveStar(),
																							   projection.get(i).getFourStar(),
																							   projection.get(i).getThreeStar(),
																							   projection.get(i).getTwoStar(),
																							   projection.get(i).getOneStar()));
			}
			try {
				average = total/distanceDate;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		
		return new StatisticsReviewResponse(total, average, mapResponse);
	}
}
