package com.internship.microservice.controller;

import com.internship.microservice.model.User;
import com.internship.microservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("users")
    public ResponseEntity<?> addUsers(@RequestBody List<User> users) {
        userService.addUsers(users);

        return ResponseEntity.ok()
                .build();
    }
}
