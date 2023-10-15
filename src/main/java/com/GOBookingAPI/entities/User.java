package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
	@Column(columnDefinition = "TEXT")
	private String password ;
	@Column( columnDefinition = "varchar(50)", unique = true)
	private String email;
	@Column(columnDefinition = "varchar(10)")
	private String phoneNumber;
//	@Column(nullable = false )
//	private Date createDate ;
	@Column
	private Boolean isNonBlock;
	
	@Column
	private String avatarUrl;

	@Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;
	    
    @Column(name = "provider_id")
	private String providerId;
	    
//	@ManyToOne
//	@JoinColumn(name = "role_id")
//	private Set<Role> role;
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;
//	@OneToOne
//	@JoinColumn(name = "customer_id" , referencedColumnName = "id")
//	private Customer customer;
//	
//	@OneToOne
//	@JoinColumn(name = "driver_id" , referencedColumnName = "id")
//	private Driver driver;
	
}
