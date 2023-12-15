package com.GOBookingAPI.repositories.projection;

public interface CustomerProjection {

	int getId();
	String getEmail();
	String getFullname();
	String getPhonenumber();
	boolean getIsnonblock();
}
