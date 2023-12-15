package com.GOBookingAPI.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.ICustomerService;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IUserService;

import jakarta.websocket.server.PathParam;
import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IDriverService driverService;
	
	@GetMapping("/get")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> index(Principal principal){
		System.out.print(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		return ResponseEntity.ok("Welcome to admin page : ");
	}
	
	@GetMapping("/customer/{offset}/{pagesize}/{field}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getCustomers(@PathVariable("offset") int offset , @PathVariable("pagesize") int pagesize
			,@PathVariable("field") String field){
		return ResponseEntity.ok(customerService.getCustomerPageAndSort(offset, pagesize, field));
	}
	
	@GetMapping("/customer/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getCustomerDetail(@PathVariable("id") int id){
		return ResponseEntity.ok(customerService.getCustomerDetailById(id));
	}
	
	@PutMapping("/user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateIsNonBlock(@PathVariable("id") int id , @RequestParam("isnonblock") boolean isnonblock){
		userService.UpdateUserIsNonBlock(isnonblock, id);
		JSONObject json = new JSONObject();
		json.put("message", "Update Complete!");
		return ResponseEntity.ok(json);
	}
	
	
	@GetMapping("/driver/{offset}/{pagesize}/{field}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getDrivers(@PathVariable("offset") int offset , @PathVariable("pagesize") int pagesize
			,@PathVariable("field") String field){
		return ResponseEntity.ok(driverService.getDriverPageAndSort(offset, pagesize, field));
	}
	
	@PutMapping("/driver/active/{ids}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> ActiveDriver(@PathVariable String ids){
		String[] idsString = ids.split(",");
		List<Integer> list = new ArrayList<Integer>();
		for(String i :idsString) {
			list.add(Integer.parseInt(i));
		}
		JSONObject json = new JSONObject();
		if(driverService.ActiveDriver(list)) {
			json.put("message", "Active Complete!");
		}else {
			json.put("message", "Active Fail!");
		}
		return ResponseEntity.ok(json);
	}
}
