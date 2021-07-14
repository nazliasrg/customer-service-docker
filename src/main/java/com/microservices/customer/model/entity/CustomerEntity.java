package com.microservices.customer.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_customer")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private Double saldo;

    @Column
    private Integer statusAccount;

    public CustomerEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
