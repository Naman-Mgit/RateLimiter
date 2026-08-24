package com.example.RateLimiter.Controller;

import com.example.RateLimiter.DTO.CreateUserRequest;
import com.example.RateLimiter.DTO.UserResponse;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request
    ) {

        User user =
                userService.createUser(request.getUsername());

        UserResponse response = new UserResponse(
                user.getId(),
                user.getUsername()
        );

        return ResponseEntity.ok(response);
    }
}