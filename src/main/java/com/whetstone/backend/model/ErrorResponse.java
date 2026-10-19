package com.whetstone.backend.model;

public record ErrorResponse(int status, String error, String message) {
    
}
