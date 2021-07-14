package com.microservices.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusMessageDto<T>{
    private Integer status;
    private String message;
    private T data;
}
