package org.richardliao.spring.controler;

import javax.validation.Valid;

import org.richardliao.spring.Spitter;
import org.richardliao.spring.data.SpitterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/spitter")
public class SpitterController {

	private SpitterRepository spitterRepository;
	
	public SpitterController() {}
	
	@Autowired
	public SpitterController(SpitterRepository spitterRepository) {
		this.spitterRepository = spitterRepository;
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegistrationForm(Model model) {
		model.addAttribute(new Spitter());
		return "registerForm";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration(@Valid Spitter spitter, Errors errors) {
		if (errors.hasErrors()) {
			return "registerForm";
		}
		this.spitterRepository.save(spitter);
		return "redirect:/spitter/" + spitter.getUsername();
	}
	
	@RequestMapping(value="/{username}", method=RequestMethod.GET)
	public String showSpitterProfile(@PathVariable String username, Model model) {
		Spitter spitter = this.spitterRepository.findByUsername(username);
		model.addAttribute(spitter);
		return "profile";
	}
}
