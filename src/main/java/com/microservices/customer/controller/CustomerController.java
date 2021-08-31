package com.microservices.customer.controller;

import com.microservices.customer.config.JwtUtils;
import com.microservices.customer.model.dto.CustomerDto;
import com.microservices.customer.model.dto.JWTResponse;
import com.microservices.customer.model.dto.StatusMessageDto;
import com.microservices.customer.model.entity.CustomerEntity;
import com.microservices.customer.repository.CustomerRepository;
import com.microservices.customer.service.CustomerDetailsImpl;
import com.microservices.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customer")
@CrossOrigin(origins = "*")
public class CustomerController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    KafkaTemplate<String, CustomerEntity> kafkaTemplate;

    private static final String TOPIC = "CustomerTopic";

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCustomer(){
        List<CustomerEntity> customerEntityList = customerRepository.findAll();
        return ResponseEntity.ok(customerEntityList);
    }

    @PutMapping("/edit-customer/{id}")
    public ResponseEntity<?> editProduct(@RequestBody CustomerDto customerDto, @PathVariable Integer id){
        StatusMessageDto result = new StatusMessageDto<>();
        try{
            return customerService.editCustomer(customerDto, id);
        }
        catch(Exception e){
            result.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/delete-customer/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id){
        StatusMessageDto result = new StatusMessageDto<>();
        try{
            return customerService.deleteCustomer(id);
        }
        catch(Exception e){
            result.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/get-customer/{username}")
    public ResponseEntity<?> getCustomer(@PathVariable String username){
        CustomerEntity customerEntity = customerRepository.findByUsername(username);
        return ResponseEntity.ok(customerEntity);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody CustomerDto dto) {
        StatusMessageDto<CustomerEntity> response = new StatusMessageDto<>();
        // checking user exist or not
        CustomerEntity customer = customerRepository.findByUsername(dto.getUsername());
        if (customer != null) {
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.setMessage("Username is exist!");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
        // register account
        try {
            CustomerEntity customerEntity = new CustomerEntity(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
//            customerEntity.setUsername(dto.getUsername());
            customerEntity.setName(dto.getName());
            customerEntity.setAddress(dto.getAddress());
            customerEntity.setEmail(dto.getEmail());
            customerEntity.setPhoneNumber(dto.getPhoneNumber());
            customerEntity.setStatusAccount(1);
            customerEntity.setSaldo(dto.getSaldo());
            customerRepository.save(customerEntity);

            customerService.createCustomer(customerEntity);

            kafkaTemplate.send(TOPIC, customerEntity);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage(customerEntity.getUsername() + " has been created successfully!");
            response.setData(customerEntity);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CustomerDto dto) {
        StatusMessageDto response = new StatusMessageDto<>();

        CustomerEntity customerEntity = customerRepository.findByUsername(dto.getUsername());

        if(customerEntity.getStatusAccount() == 0){
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.setMessage("Account is not active!");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }

        try {
            // autentikasi user
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // generate token
            String jwt = jwtUtils.generateJwtToken(authentication);
            // get user principal
            CustomerDetailsImpl customerDetails = (CustomerDetailsImpl) authentication.getPrincipal();

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Login success!");
            response.setData(new JWTResponse(jwt, customerDetails.getUsername()));

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
        }
    }
}
