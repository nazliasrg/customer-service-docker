package com.microservices.customer.listener;

import com.microservices.customer.model.dto.CustomerDto;
import com.microservices.customer.model.dto.StatusMessageDto;
import com.microservices.customer.repository.CustomerRepository;
import com.microservices.customer.service.CustomerService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @KafkaListener(topics = "CustomerTopic", groupId = "group_customer2")
    public void consumeCustomer(String customer){
        StatusMessageDto result = new StatusMessageDto<>();

        JSONObject customerJson = new JSONObject(customer);

        CustomerDto dto = new CustomerDto();

        System.out.println(customerRepository.findByUsername(customerJson.getString("username")));

        dto.setUsername(customerJson.getString("username"));

        if(customerRepository.findByUsername(customerJson.getString("username")) != null){
            dto.setPassword(customerJson.getString("password"));
            dto.setName(customerJson.getString("name"));
            dto.setEmail(customerJson.getString("email"));
            dto.setAddress(customerJson.getString("address"));
            dto.setPhoneNumber(customerJson.getString("phoneNumber"));
            dto.setStatusAccount(customerJson.getInt("statusAccount"));
            dto.setSaldo(customerJson.getDouble("saldo"));

            result.setMessage(customerService.editCustomerFromTopic(dto));

            System.out.println(" ========================================================= " );
            System.out.println("\t Customer data : " + customer);
            System.out.println("\t " + result.getMessage());
            System.out.println(" ========================================================= ");
        }
        else{
            dto.setPassword(customerJson.getString("password"));
            dto.setName(customerJson.getString("name"));
            dto.setEmail(customerJson.getString("email"));
            dto.setAddress(customerJson.getString("address"));
            dto.setPhoneNumber(customerJson.getString("phoneNumber"));
            dto.setStatusAccount(customerJson.getInt("statusAccount"));
            dto.setSaldo(customerJson.getDouble("saldo"));

            result.setMessage(customerService.addCustomerFromTopic(dto));

            System.out.println(" ========================================================= " );
            System.out.println("\t Customer data : " + customer);
            System.out.println("\t " + result.getMessage());
            System.out.println(" ========================================================= ");
        }
    }
}
