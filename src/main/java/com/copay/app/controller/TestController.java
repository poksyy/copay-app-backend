package com.copay.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/response")
    public String testResponse() {
        return "Connection successful!";
    }
}