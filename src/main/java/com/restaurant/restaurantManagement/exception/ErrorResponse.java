package com.restaurant.restaurantManagement.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private String timestamp;
}
