package com.example.filter_and_interceptor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DefaultController {
    @GetMapping("/api/v1/api1")
    public String api_1() {
        return "API_1";
    }

    @GetMapping("/api/v1/api2")
    public String api_2() {
        return "API_2";
    }

    @PostMapping("/api/v1/api3")
    public ResponseEntity api_3(String param) {
        return ResponseEntity.ok(param);
    }
}
