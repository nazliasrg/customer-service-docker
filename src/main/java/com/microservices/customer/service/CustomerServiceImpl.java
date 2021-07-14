package com.microservices.customer.service;

import com.microservices.customer.model.dto.CustomerDto;
import com.microservices.customer.model.dto.StatusMessageDto;
import com.microservices.customer.model.entity.CustomerEntity;
import com.microservices.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements UserDetailsService, CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    KafkaTemplate<String, CustomerEntity> kafkaTemplate;

    private static final String TOPIC = "CustomerTopic";

    @Override
    public ResponseEntity<?> addCustomer(CustomerDto dto) {
        StatusMessageDto<CustomerEntity> result = new StatusMessageDto<>();

        if(dto.getUsername() != null){
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setUsername(dto.getUsername());
            customerEntity.setName(dto.getName());
            customerEntity.setAddress(dto.getAddress());
            customerEntity.setEmail(dto.getEmail());
            customerEntity.setPhoneNumber(dto.getPhoneNumber());
            customerEntity.setStatusAccount(1);
            customerEntity.setSaldo(dto.getSaldo());
            customerRepository.save(customerEntity);

            kafkaTemplate.send(TOPIC, customerEntity);

            result.setStatus(HttpStatus.OK.value());
            result.setMessage(customerEntity.getUsername() + " has been added successfully!");
            result.setData(customerEntity);
            return ResponseEntity.ok(result);
        }
        else{
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            result.setMessage("username is empty!");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Override
    public ResponseEntity<?> editCustomer(CustomerDto dto, Integer id) {
        StatusMessageDto<CustomerEntity> result = new StatusMessageDto<>();

        CustomerEntity customerEntity = customerRepository.findByCustomerId(id);

        if(dto.getUsername() != null){
            customerEntity.setUsername(dto.getUsername());
        }

        if(dto.getName() != null){
            customerEntity.setName(dto.getName());
        }

        if(dto.getAddress() != null){
            customerEntity.setAddress(dto.getAddress());
        }

        if(dto.getEmail() != null){
            customerEntity.setEmail(dto.getEmail());
        }

        if(dto.getPhoneNumber() != null){
            customerEntity.setPhoneNumber(dto.getPhoneNumber());
        }

        if(dto.getStatusAccount() != null){
            customerEntity.setStatusAccount(dto.getStatusAccount());
        }

        if(dto.getSaldo() != null){
            customerEntity.setSaldo(dto.getSaldo());
        }

        customerRepository.save(customerEntity);

        kafkaTemplate.send(TOPIC, customerEntity);

        result.setStatus(HttpStatus.OK.value());
        result.setMessage("Customer " + customerEntity.getUsername() + " has been changed successfully!");
        result.setData(customerEntity);

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> deleteCustomer(Integer id) {
        StatusMessageDto result = new StatusMessageDto<>();

        CustomerEntity customerEntity = customerRepository.findByCustomerId(id);
        customerEntity.setStatusAccount(0);
        customerRepository.save(customerEntity);

        kafkaTemplate.send(TOPIC, customerEntity);

        result.setStatus(HttpStatus.OK.value());
        result.setMessage("Customer " + customerEntity.getUsername() + " data has been deleted!");
        result.setData(customerEntity);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public CustomerEntity createCustomer(CustomerEntity customer) {
        return customerRepository.save(customer);
    }

    @Override
    public CustomerEntity loginCustomer(String username, String password) {
        CustomerEntity customer = customerRepository.findByUsername(username);
        return customer;
    }

    @Override
    public String editCustomerFromTopic(CustomerDto dto) {
        StatusMessageDto<CustomerDto> result = new StatusMessageDto<>();

        CustomerEntity customerEntity = customerRepository.findByUsername(dto.getUsername());
        customerEntity.setPassword(dto.getPassword());
        customerEntity.setName(dto.getName());
        customerEntity.setEmail(dto.getEmail());
        customerEntity.setAddress(dto.getAddress());
        customerEntity.setPhoneNumber(dto.getPhoneNumber());
        customerEntity.setStatusAccount(dto.getStatusAccount());
        customerEntity.setSaldo(dto.getSaldo());

        customerRepository.save(customerEntity);

        result.setMessage("Customer " + customerEntity.getUsername() + " has been updated successfully!");
        return result.getMessage();
    }

    @Override
    public String addCustomerFromTopic(CustomerDto dto) {
        StatusMessageDto<CustomerDto> result = new StatusMessageDto<>();

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUsername(dto.getUsername());
        customerEntity.setPassword(dto.getPassword());
        customerEntity.setName(dto.getName());
        customerEntity.setEmail(dto.getEmail());
        customerEntity.setAddress(dto.getAddress());
        customerEntity.setPhoneNumber(dto.getPhoneNumber());
        customerEntity.setStatusAccount(dto.getStatusAccount());
        customerEntity.setSaldo(dto.getSaldo());

        customerRepository.save(customerEntity);

        result.setMessage("Customer " + customerEntity.getUsername() + " has been added successfully!");
        return result.getMessage();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomerEntity customer = customerRepository.findByUsername(username);

        return CustomerDetailsImpl.build(customer);
    }
}
