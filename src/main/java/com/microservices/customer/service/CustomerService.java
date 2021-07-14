package com.microservices.customer.service;

import com.microservices.customer.model.dto.CustomerDto;
import com.microservices.customer.model.entity.CustomerEntity;
import org.springframework.http.ResponseEntity;

public interface CustomerService {
    ResponseEntity<?> addCustomer(CustomerDto customerDto);
    ResponseEntity<?> editCustomer(CustomerDto customerDto, Integer consumerId);
    ResponseEntity<?> deleteCustomer(Integer consumerId);

    CustomerEntity createCustomer(CustomerEntity customerEntity);
    CustomerEntity loginCustomer(String username, String password);

    String editCustomerFromTopic(CustomerDto customerDto);
    String addCustomerFromTopic(CustomerDto customerDto);
}
