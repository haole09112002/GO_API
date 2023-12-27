package com.GOBookingAPI.services.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
		Long amount= 0L ;
		int total = 0;
		double average =0;  
		
		 List<String> timeStamps = new ArrayList<String>();;
		
		 List<Long> amounts = new ArrayList<Long>();
		
		 List<Integer> totals = new ArrayList<Integer>();
		
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
			int monthOfYear = 0;
			LocalDate localDate = from.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

			if(localDate.getYear()!= (calFrom.get(Calendar.YEAR))) {
				calFrom.setTime(from);
				monthOfYear = calFrom.getActualMaximum(Calendar.MONTH) +1;
			}else {
				monthOfYear = calFrom.get(Calendar.MONTH) + 1;
			}
			int year = calFrom.get(Calendar.YEAR) ;
			List<StatisticsPaymentMonthProjection> projection = paymentRepository.getStatisticsMonthOfYear(year);
			for(int i = 0 ; i< projection.size() ; i++) {
				
				amount += projection.get(i).getAmount();
				total += projection.get(i).getTotal();
				timeStamps.add(String.valueOf(projection.get(i).getMonth()));
				amounts.add(projection.get(i).getAmount());
				totals.add(projection.get(i).getTotal());
			}
			try {
				average = amount/monthOfYear;
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
			int daysInMonth = 0;
			if(from.getMonth()+1 != (calFrom.get(Calendar.MONTH)+1)) {
				calFrom.setTime(from);
				daysInMonth = calFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
			}else {
				daysInMonth = calFrom.get(Calendar.DAY_OF_MONTH);
			}
			int month = calFrom.get(Calendar.MONTH) + 1;
			int year = calFrom.get(Calendar.YEAR) ;
			
			List<StatisticsPaymentDayProjection> projection = paymentRepository.getStatisticsDateofMonth(month,year);
			for(int i = 0 ; i< projection.size() ; i++) {
				
			
			
				amount += projection.get(i).getAmount();
				total += projection.get(i).getTotal();
				timeStamps.add(projection.get(i).getDate().toString());
				amounts.add(projection.get(i).getAmount());
				totals.add(projection.get(i).getTotal());
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
				timeStamps.add(projection.get(i).getDate().toString());
				amounts.add(projection.get(i).getAmount());
				totals.add(projection.get(i).getTotal());
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
		
		return new StatisticsPaymentResponse(amount, average, total, new BasePaymentResponse(timeStamps,amounts,totals));
	}
	
	private StatisticsBookingResponse getStatisticsBooking(Date from, Date to, String unit) {
		int total = 0;
		int success = 0;
		int cancelled = 0;
		double average =0;  
		 List<String> timeStamps = new ArrayList<String>();
		 List<Integer> totals  = new ArrayList<Integer>();
		 List<Integer> successes  =new ArrayList<Integer>();
		 List<Integer> cancelleds = new ArrayList<Integer>();
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
			int monthOfYear = 0;
			LocalDate localDate = from.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

			if(localDate.getYear()!= (calFrom.get(Calendar.YEAR))) {
				calFrom.setTime(from);
				monthOfYear = calFrom.getActualMaximum(Calendar.MONTH) +1;
			}else {
				monthOfYear = calFrom.get(Calendar.MONTH) + 1;
			}
			int year = calFrom.get(Calendar.YEAR) ;
			List<StatisticsBookingCountProjections> countComplete = bookingRepository.getCountByStatusMonthOfYear( year,BookingStatus.COMPLETE.toString());
			List<StatisticsBookingCountProjections> countCancel= bookingRepository.getCountByStatusMonthOfYear(year,BookingStatus.CANCELLED.toString());
			List<StatisticsBookingCountProjections> count = bookingRepository.getCountByStatusMonthOfYear( year,"");
			List<StatisticsBookingCountProjections> countRefunded= bookingRepository.getCountByStatusMonthOfYear(year,BookingStatus.REFUNDED.toString());
			
			for(int i = 0 ; i< count.size() ; i++) {
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
				timeStamps.add(count.get(i).getDay().toString());
				totals.add(count.get(i).getCount());
				successes.add(datesucess);
				cancelleds.add(datecancle+daterefunded);
			}
			
			try {
				average = total/monthOfYear;
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
			int daysInMonth = 0;
			if(from.getMonth()+1 != (calFrom.get(Calendar.MONTH)+1)) {
				calFrom.setTime(from);
				daysInMonth = calFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
			}else {
				daysInMonth = calFrom.get(Calendar.DAY_OF_MONTH);
			}
			int month = calFrom.get(Calendar.MONTH) + 1;
			int year = calFrom.get(Calendar.YEAR) ;
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
				timeStamps.add(count.get(i).getDay().toString());
				totals.add(count.get(i).getCount());
				successes.add(datesucess);
				cancelleds.add(datecancle+daterefunded);
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
				
				timeStamps.add(count.get(i).getDay().toString());
				totals.add(count.get(i).getCount());
				successes.add(datesucess);
				cancelleds.add(datecancle+daterefunded);
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
		
		return new StatisticsBookingResponse(total, average, success,cancelled, new BaseBookingResponse(timeStamps,totals,successes,cancelleds));
	}
	
	private StatisticsReviewResponse getStatisticsReview(Date from, Date to, String unit) {
		int total = 0;
		double average =0;  
		int fiveStar = 0;
		int fourStar = 0;
		int threeStar = 0 ;
		int twoStar = 0;
		int oneStar = 0;
		int sum = 0;
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
			
			LocalDate localDate = from.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

			if(localDate.getYear()!= (calFrom.get(Calendar.YEAR))) 
				calFrom.setTime(from);
			int year = calFrom.get(Calendar.YEAR) ;
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewMonth(year);
			for(int i = 0 ; i< projection.size() ; i++) {
				total+= projection.get(i).getCount();
				fiveStar += projection.get(i).getFiveStar();
				fourStar+= projection.get(i).getFourStar();
				threeStar += projection.get(i).getThreeStar();
				twoStar +=projection.get(i).getTwoStar();
				oneStar += projection.get(i).getOneStar();
				sum += projection.get(i).getFiveStar()*5 +  projection.get(i).getFourStar()*4 + projection.get(i).getThreeStar()*3 + projection.get(i).getTwoStar()*2 +projection.get(i).getOneStar() ;
			}
			try {
				average = sum/total ;
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
			if(from.getMonth()+1 != (calFrom.get(Calendar.MONTH)+1)) 
				calFrom.setTime(from);
			int month = calFrom.get(Calendar.MONTH) + 1;
			int year = calFrom.get(Calendar.YEAR) ;
			List<StatisticsReviewBaseProjection> projection = reviewRepository.getStatisticsReviewDateOfMonth(month, year);
			for(int i = 0 ; i< projection.size() ; i++) {
				total+= projection.get(i).getCount();
				total+= projection.get(i).getCount();
				fiveStar += projection.get(i).getFiveStar();
				fourStar+= projection.get(i).getFourStar();
				threeStar += projection.get(i).getThreeStar();
				twoStar +=projection.get(i).getTwoStar();
				oneStar += projection.get(i).getOneStar();
				sum += projection.get(i).getFiveStar()*5 +  projection.get(i).getFourStar()*4 + projection.get(i).getThreeStar()*3 + projection.get(i).getTwoStar()*2 +projection.get(i).getOneStar() ;
				
			}
			try {
				average = sum/total;
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
				total+= projection.get(i).getCount();
				fiveStar += projection.get(i).getFiveStar();
				fourStar+= projection.get(i).getFourStar();
				threeStar += projection.get(i).getThreeStar();
				twoStar +=projection.get(i).getTwoStar();
				oneStar += projection.get(i).getOneStar();
				sum += projection.get(i).getFiveStar()*5 +  projection.get(i).getFourStar()*4 + projection.get(i).getThreeStar()*3 + projection.get(i).getTwoStar()*2 +projection.get(i).getOneStar() ;
				
			}
			try {
				average = sum/total;
			} catch (Exception e) {
				average = 0;
			}
			break;
		}
		default:
			throw new BadRequestException("Invalid statisticsField ");
		}
		
		return new StatisticsReviewResponse(total, average, new BaseReviewResponse(fiveStar,fourStar,threeStar,twoStar,oneStar));
	}
}
