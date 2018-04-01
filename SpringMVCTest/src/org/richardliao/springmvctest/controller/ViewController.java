package org.richardliao.springmvctest.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {
	@RequestMapping("/view")
	public ModelAndView view(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home");
		return mav;
	}
}
