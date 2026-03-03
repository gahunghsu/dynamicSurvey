package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public AppResponse<User> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return AppResponse.error(RspCode.UNAUTHORIZED);
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(AppResponse::success)
                .orElse(AppResponse.error(RspCode.NOT_FOUND, "User not found"));
    }
}
