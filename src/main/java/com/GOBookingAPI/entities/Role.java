package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Role")
public class Role implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id ;

	@Column(nullable = false , columnDefinition = "Varchar(30)")
	private String name;
	
//	@OneToMany(mappedBy = "role")
//	private List<User> users = new ArrayList<>();
}
