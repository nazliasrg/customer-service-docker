package com.microservices.customer.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JWTResponse {
    private String token;
    private String username;
    private String type = "Bearer";

    public JWTResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
