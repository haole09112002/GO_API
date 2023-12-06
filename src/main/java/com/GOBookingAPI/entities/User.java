package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.GOBookingAPI.exceptions.NotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@ToString
@Table(name = "User")
public class User implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column( columnDefinition = "varchar(50)", unique = true)
	private String email;
	@Column(columnDefinition = "varchar(10)")
	private String phoneNumber;
	@Column
	private Date createDate ;
	@Column
	private Boolean isNonBlock;
	
	@Column(columnDefinition = "longtext")
	private String avatarUrl;
	    
	    
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;
	
	@OneToOne(mappedBy = "user")
	private Customer customer;
	
	@OneToOne(mappedBy = "user")
	private Driver driver;


	public Role getFirstRole(){
		Iterator<Role> roleIterator = this.roles.iterator();
		if (roleIterator.hasNext()) {
			return roleIterator.next();
		} else {
			throw new NotFoundException("Không có role");
		}
	}
	
}
