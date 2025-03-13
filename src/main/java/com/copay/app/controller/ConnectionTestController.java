package com.copay.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionTestController {

	@GetMapping("/api/response")
	public String ConnectiontestResponse() {
		return "Connection successful!";
	}
}