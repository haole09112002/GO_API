package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "User")
public class User implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable =  false , columnDefinition = "varchar(50)" , unique = true)
	private String username ;
	@Column(nullable = false , columnDefinition = "TEXT")
	private String password ;
	@Column(nullable = false , columnDefinition = "varchar(50)", unique = true)
	private String email;
	@Column(nullable = false , columnDefinition = "varchar(10)")
	private String phoneNumber;
	@Column(nullable = false )
	private Date createDate ;
	@Column(nullable = false)
	private Boolean isNonBlock;
	
	@Column
	private String avatarUrl;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	@OneToOne
	@JoinColumn(name = "customer_id" , referencedColumnName = "id")
	private Customer customer;
	
	@OneToOne
	@JoinColumn(name = "driver_id" , referencedColumnName = "id")
	private Driver driver;
	
}
