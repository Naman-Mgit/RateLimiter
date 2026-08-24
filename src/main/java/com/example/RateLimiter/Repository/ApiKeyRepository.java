package com.example.RateLimiter.Repository;

import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyValue(String keyValue); 

    long countByUserId(Long userId);

    List<ApiKey> findByUser(User user);
}