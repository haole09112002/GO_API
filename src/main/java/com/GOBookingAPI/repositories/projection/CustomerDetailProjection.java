package com.GOBookingAPI.repositories.projection;

import java.sql.Timestamp;
import java.util.Date;


public interface CustomerDetailProjection {
	int getId();
	Date getCreate_date();
	String getEmail();
	boolean getIs_non_block();
	String getPhone_number();
	Date getDate_of_birth();
	String getFull_name();
	boolean getGender();
}
