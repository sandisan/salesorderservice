package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.demo.domain.Customer;

/**
 * @author Abesh
 *
 */
@Service
public class CustomerService {

	@Autowired
	private RestTemplate restTemplate;

	private final static String URL = "http://customerservice-app-cicd.169-61-227-230.nip.io/customer/";

	public Boolean isCustomerValid(Long id) {

		Customer customer = restTemplate.getForObject(URL + id, Customer.class);

		return (null != customer);

	}

}
