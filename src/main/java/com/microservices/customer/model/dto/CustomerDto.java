package com.microservices.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private String username;
    private String password;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    private Double saldo;
    private Integer statusAccount;

    @Override
    public String toString() {
        return "CustomerDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", saldo=" + saldo +
                ", statusAccount=" + statusAccount +
                '}';
    }
}
