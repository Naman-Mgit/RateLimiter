package com.example.RateLimiter.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime lastUsedAt;


    public ApiKey() {
    }


    public ApiKey(String keyValue, User user) {

        this.keyValue = keyValue;
        this.user = user;

        this.active = true;

        this.createdAt = LocalDateTime.now();

        // For now, key expires after 30 days
        this.expiresAt = LocalDateTime.now().plusDays(30);

        this.lastUsedAt = null;
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

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}