package com.example.RateLimiter.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ApiKey() {
    }

    public ApiKey(String keyValue, User user) {
        this.keyValue = keyValue;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public User getUser() {
        return user;
    }
}