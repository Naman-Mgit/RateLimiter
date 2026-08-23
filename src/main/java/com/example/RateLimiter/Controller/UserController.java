package com.example.RateLimiter.Controller;

import com.example.RateLimiter.Model.User;
import com.example.RateLimiter.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
   private  final UserService userService;

   public  UserController(UserService userService){
        this.userService=userService;
   }

   @PostMapping
    public ResponseEntity<User> createUser(@RequestParam String username){
       User user = userService.createUser(username);

       return ResponseEntity.ok(user);
   }
}
