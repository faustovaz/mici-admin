package com.mici.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mici.service.DashboardService;

@Controller
public class MainController {

	private DashboardService service;

	public MainController(DashboardService service) {
		this.service = service;
	}
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("dashboard", service.getDashboard());
		return "index";
	}
	
}
