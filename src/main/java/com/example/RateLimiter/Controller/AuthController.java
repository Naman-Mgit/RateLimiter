package com.example.RateLimiter.Controller;

import com.example.RateLimiter.DTO.LoginRequest;
import com.example.RateLimiter.DTO.LoginResponse;
import com.example.RateLimiter.DTO.RegisterRequest;
import com.example.RateLimiter.DTO.UserResponse;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Service.JwtService;
import com.example.RateLimiter.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService=jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequest request
    ) {

        User user = userService.registerUser(
                request.getUsername(),
                request.getPassword()
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getUsername()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        // Verify username and password
        User user = userService.loginUser(
                request.getUsername(),
                request.getPassword()
        );

        // Generate JWT
        String token = jwtService.generateToken(
                user.getUsername()
        );

        // Return JWT
        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }
}