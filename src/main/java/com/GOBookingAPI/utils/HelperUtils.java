package com.GOBookingAPI.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


public class HelperUtils {
	public static final ObjectWriter JSON_WRITTER = new ObjectMapper().writer().withDefaultPrettyPrinter();
}
