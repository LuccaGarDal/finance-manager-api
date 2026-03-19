package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/login")
    private User login (@RequestBody User user) {
        return userRepository.save(user);
    }
}
