package com.microservices.customer.repository;

import com.microservices.customer.model.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    CustomerEntity findByCustomerId(Integer customerId);
    CustomerEntity findByUsername(String username);
}
