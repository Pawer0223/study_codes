package com.example.jwt_service.controller;

import com.example.jwt_service.model.User;
import com.example.jwt_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RestApiController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("join")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        userRepository.save(user);
        return "create " + user.getName();
    }

    // user, manager, admin
    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }

    // manager, admin
    @GetMapping("/api/v1/user/manager")
    public String manager() {
        return "manager";
    }

    // admin
    @GetMapping("/api/v1/user/admin")
    public String admin() {
        return "admin";
    }
}
