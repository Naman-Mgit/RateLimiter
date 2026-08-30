package com.example.RateLimiter.Controller;

import com.example.RateLimiter.DTO.ApiKeyResponse;
import com.example.RateLimiter.Model.ApiKey;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Service.ApiKeyService;
import com.example.RateLimiter.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final UserService userService;

    public ApiKeyController(ApiKeyService apiKeyService,UserService userService) {

        this.apiKeyService = apiKeyService;
        this.userService=userService;
    }


    @PostMapping
    public ResponseEntity<ApiKeyResponse> createApiKey() {

        // Get logged-in user's authentication
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // Get username stored by JwtAuthenticationFilter
        String username = authentication.getName();

        // Find user in database
        User user = userService.getUserByUsername(username);

        // Create API key for logged-in user
        ApiKey apiKey =
                apiKeyService.createApiKey(user.getId());

        return ResponseEntity.ok(
                convertToResponse(apiKey)
        );
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> getApiKeys(

    ) {
        //Extracting username from the jwt token after user has logged in

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User user =
                userService.getUserByUsername(username);

        List<ApiKeyResponse> response = apiKeyService
                .getApiKeysByUser(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<String> revokeApiKey(
            @PathVariable Long apiKeyId
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User user =
                userService.getUserByUsername(username);

        apiKeyService.revokeApiKey(
                apiKeyId,
                user.getId()
        );

        return ResponseEntity.ok(
                "API key revoked successfully"
        );
    }

    private ApiKeyResponse convertToResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getKeyValue(),
                apiKey.getUser().getId(),
                apiKey.isActive(),
                apiKey.getCreatedAt(),
                apiKey.getExpiresAt(),
                apiKey.getLastUsedAt()
        );
    }
}