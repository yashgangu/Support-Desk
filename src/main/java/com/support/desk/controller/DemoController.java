package com.support.desk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

	@GetMapping("/hello")
	public String sayHello() {
	return "Controller layer working fine!";
	}
}
