package com.example.RateLimiter.Service;

import com.example.RateLimiter.Exception.InvalidCredentialsException;
import com.example.RateLimiter.Exception.UserNotFoundException;
import com.example.RateLimiter.Exception.UsernameAlreadyExistsException;
import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // OLD USER CREATION - keep temporarily
    public User createUser(String username) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists"
            );
        }

        User user = new User(username);

        return userRepository.save(user);
    }


    // NEW REGISTRATION METHOD
    public User registerUser(
            String username,
            String password
    ) {

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists"
            );
        }

        // Convert normal password into BCrypt hash
        String encodedPassword =
                passwordEncoder.encode(password);

        // Create user with encoded password
        User user = new User(
                username,
                encodedPassword
        );

        // Save user in database
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {

        // 1. Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password"
                        )
                );

        // 2. Compare normal password with BCrypt hash
        boolean passwordMatches = passwordEncoder.matches(
                password,
                user.getPassword()
        );

        // 3. Reject if password is wrong
        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }

        // 4. Login successful
        return user;
    }

    public User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );
    }
}