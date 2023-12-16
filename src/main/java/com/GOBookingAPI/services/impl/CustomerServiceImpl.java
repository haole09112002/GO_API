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
import com.GOBookingAPI.repositories.projection.CustomerDetailProjection;
import com.GOBookingAPI.repositories.projection.CustomerProjection;
import com.GOBookingAPI.services.ICustomerService;
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
	public CustomerDetailResponse getCustomer(int id) {
//	Customer customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
		return null;
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
				userJoin.get("id").alias("Id"),
				userJoin.get("email").alias("Email"),
				root.get("fullName").alias("FullName"),
				userJoin.get("phoneNumber").alias("PhoneNumber"),
				userJoin.get("isNonBlock").alias("IsNonBlock")
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
			sortRoute = userJoin.get(sortField);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid sortField" + sortField);
		}
		Order order = "asc".equalsIgnoreCase(sortField) ? criteriaBuilder.asc(sortRoute) : criteriaBuilder.desc(sortRoute);
		criteriaQuery.orderBy(order);
		if(keyword != null ) {
			if(searchField.equals("email")) {
				Path<String> fieldEmail = userJoin.get("email");
				Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(fieldEmail), "%"+keyword.toLowerCase() +"%" );
				predicates.add(predicate);
			} else if(searchField.equals("fullname")) {
				Path<String> fieldName = root.get("full_name");
				Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(fieldName),"%"+ keyword.toLowerCase() + "%");
				predicates.add(predicate);
			}else {
				Path<String> fieldPhone = userJoin.get("phone_number");
				Predicate predicate = criteriaBuilder.like(fieldPhone, keyword.toLowerCase() + "%");
				predicates.add(predicate);
			}
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
		
		 AppUtils.validatePageNumberAndSize(page, size);

        int totalResults = typedQuery.getResultList().size();

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        
        List<CustomersResponse> customers = typedQuery.getResultList().stream().map(result -> new CustomersResponse((int) result.get("Id"),
        																						(String) result.get("Email"),
        																						(String) result.get("FullName"),
        																						(String) result.get("PhoneNumber"),
        																						(Boolean) result.get("IsNonBlock"))).collect(Collectors.toList());
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomersResponse> pageResponse = new PageImpl<CustomersResponse>(customers, pageRequest ,totalResults);
        
        return new PagedResponse<CustomersResponse>(pageResponse.getContent() , pageResponse.getNumber() ,pageResponse.getSize(),
        											pageResponse.getTotalElements(), pageResponse.getTotalPages(), pageResponse.isLast());
	}

	@Override
	public CustomerDetailResponse getCustomerDetailById(int id) {
		ModelMapper modelMap = new ModelMapper();
		CustomerDetailProjection customerDetailProjection = customerRepository.findByIdByAdmin(id);
		modelMap.map(customerDetailProjection, CustomerDetailProjection.class);
		return new CustomerDetailResponse(
				customerDetailProjection.getId(),
				customerDetailProjection.getEmail(),
				customerDetailProjection.getFull_name(),
				customerDetailProjection.getPhone_number(),
				customerDetailProjection.getIs_non_block(),
				customerDetailProjection.getCreate_date() ,
				customerDetailProjection.getDate_of_birth(),
				customerDetailProjection.getGender());
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
