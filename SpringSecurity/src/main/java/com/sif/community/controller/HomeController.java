package com.sif.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(@RequestParam(value = "EMAIL_AUTH", required = false) boolean email_auth, Model model) {
		model.addAttribute("EMAIL_AUTH", email_auth);
		return "home";
	}
	
}
