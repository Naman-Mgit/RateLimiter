package com.example.RateLimiter.DTO;

public class ApiKeyResponse {

    private Long id;
    private String keyValue;
    private Long userId;

    public ApiKeyResponse(Long id, String keyValue, Long userId) {
        this.id = id;
        this.keyValue = keyValue;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public Long getUserId() {
        return userId;
    }
}