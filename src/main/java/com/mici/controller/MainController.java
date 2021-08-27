package com.mici.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mici.service.DashboardService;

@Controller
public class MainController {

	private DashboardService service;

	public MainController(DashboardService service) {
		this.service = service;
	}
	
	@GetMapping("/")
	public String index(Authentication user, Model model) {
		model.addAttribute("dashboard", service.getDashboard());
		return "index";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	@ModelAttribute
	public void addAttributes(Model model, Authentication user) {
		model.addAttribute("sessionUser", user != null ? user.getName() : "");
	}
	
}
