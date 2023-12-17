package com.GOBookingAPI.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.ChangeCustomerInfoRequest;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.utils.AppUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
	private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;
    @Override
    public CustomerResponse getById(int id) {
        Customer customer = customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
   		return new CustomerResponse(customer);
    }


	@Override
    public CustomerBaseInfoResponse getBaseInfoById(int id) {
        Customer customer = customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
        CustomerBaseInfoResponse baseInfo = new CustomerBaseInfoResponse();
        baseInfo.setId(customer.getId());
        baseInfo.setFullName(customer.getFullName());
        baseInfo.setGender(customer.getGender());
        baseInfo.setAvatarUrl(customer.getUser().getAvatarUrl());
        baseInfo.setPhoneNumber(customer.getUser().getPhoneNumber());
        return baseInfo;
    }


	@Override
	public PagedResponse<CustomersResponse> getCustomerPageAndSort(Date from, Date to, Boolean isNonBlock, 
		 String searchField,String keyword, String sortType, String sortField, int size, int page) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<Customer> root = criteriaQuery.from(Customer.class);
		
		Join<Customer, User> userJoin = root.join("user", JoinType.INNER);
		
		criteriaQuery.multiselect(
				userJoin.alias("user"),
				root.alias("customer")
				);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		if(from != null && to != null) {
			Path<Date> fieldcreateDate = userJoin.get("createDate");
			Predicate predicate1 = criteriaBuilder.greaterThanOrEqualTo(fieldcreateDate, from);
			Predicate predicate2 = criteriaBuilder.lessThanOrEqualTo(fieldcreateDate, to);
			predicates.add(predicate1);
			predicates.add(predicate2);
		}
		
		
		Path<Boolean> fieldIsNonBlock = userJoin.get("isNonBlock");
		Predicate predicateboolean = criteriaBuilder.equal(fieldIsNonBlock, isNonBlock);
		predicates.add(predicateboolean);
		
		if(sortField == null) {
			sortField = "id";
		}
		
		if(sortType == null) {
			sortType = "asc";
		}
		
		Path<Object> sortRoute =null ;
		
		try {
			if(sortField.equals("fullName")) {
				sortRoute = root.get(sortField);
			}else {
				sortRoute = userJoin.get(sortField);
			}
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid sortField" + sortField);
		}
		Order order = "asc".equalsIgnoreCase(sortType) ? criteriaBuilder.asc(sortRoute) : criteriaBuilder.desc(sortRoute);
		criteriaQuery.orderBy(order);
		if(keyword != null ) {
			if(searchField.equals("email")) {
				Path<String> fieldEmail = userJoin.get("email");
				Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(fieldEmail), "%"+keyword.toLowerCase() +"%" );
				predicates.add(predicate);
			} else if(searchField.equals("fullName")) {
				Path<String> fieldName = root.get("fullName");
				Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(fieldName),"%"+ keyword.toLowerCase() + "%");
				predicates.add(predicate);
			}else if(searchField.equals("phoneNumber")) {
				Path<String> fieldPhone = userJoin.get("phoneNumber");
				Predicate predicate = criteriaBuilder.like(fieldPhone, keyword.toLowerCase() + "%");
				predicates.add(predicate);
			}else {
				throw new BadRequestException("Invalid field" + searchField);
			}
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
		
		 AppUtils.validatePageNumberAndSize(page, size);

        int totalResults = typedQuery.getResultList().size();

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        List<CustomersResponse> customers = new ArrayList<CustomersResponse>();
        for(Tuple tuple : typedQuery.getResultList()) {
        	User user = tuple.get("user", User.class);
        	Customer customer = tuple.get("customer", Customer.class);
        	customers.add(new CustomersResponse(user.getId(), user.getEmail(), user.getPhoneNumber(), user.getCreateDate(),
        										user.getIsNonBlock(),user.getAvatarUrl(),customer.getFullName(),
        										customer.getGender(), customer.getDateOfBirth()));
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomersResponse> pageResponse = new PageImpl<CustomersResponse>(customers, pageRequest ,totalResults);
        
        return new PagedResponse<CustomersResponse>(pageResponse.getContent() , pageResponse.getNumber() ,pageResponse.getSize(),
        											pageResponse.getTotalElements(), pageResponse.getTotalPages(), pageResponse.isLast());
	}


	@Override
	@Transactional
	public CustomerResponse changeInfo(int id, String email, ChangeCustomerInfoRequest req) {
    	Customer customer = customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));

    	if(customer.getId() != id){
			throw new NotFoundException("Not found customer id: " + id);
		}

    	if(req.getDateOfBirth() != null)
			customer.setDateOfBirth(req.getDateOfBirth());
		if(req.getFullName() != null && !req.getFullName().isBlank())
			customer.setFullName(req.getFullName());
		if(req.getGender() != null)
    		customer.setGender(req.getGender());


    	if(req.getAvatar() != null && !req.getAvatar().isEmpty()){
			String url = fileStorageService.createImgUrl(req.getAvatar());
			customer.getUser().setAvatarUrl(url);
			userRepository.save(customer.getUser());
		}
    	customerRepository.save(customer);
		return new CustomerResponse(customer);
	}
}
