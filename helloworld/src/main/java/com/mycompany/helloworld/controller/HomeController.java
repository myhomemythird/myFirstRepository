package com.mycompany.helloworld.controller;

import java.util.Date;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;


@Controller
public class HomeController {

    @RequestMapping("/")
    @ResponseBody
	public String home() {
	    return "Hello Spring Boot~";
	}
	
	@RequestMapping("/now")
	public String now(Model model) {
	    model.addAttribute("now", (new Date()).toLocaleString());
	    return "now";
	}
}