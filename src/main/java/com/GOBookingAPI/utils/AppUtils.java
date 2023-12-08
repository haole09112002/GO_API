package com.GOBookingAPI.utils;

import com.GOBookingAPI.exceptions.AppException;
import com.GOBookingAPI.exceptions.BadRequestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {
    public static void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException( "Page number cannot be less than zero.");
        }

        if (size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    public static Date convertStringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }

    /*
        @author: HaoLV
        @description:  yyyyMMddHHmmss => Date()
    */
    public static  Date convertTimeStringVNPayToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AppException("Invalid date format");
        }
    }

    public static long currentTimeInSecond() {
        return System.currentTimeMillis() / 1000;
    }
}