package org.richardliao.spring.controler;

import java.util.List;

import org.richardliao.spring.Spittle;
import org.richardliao.spring.data.SpittleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/spittles")
public class SpittleController {

	private static final String  MAX_LONG_AS_STRING = Long.MAX_VALUE + "";
	private SpittleRepository spittleRepository;
	
	@Autowired
	public SpittleController(SpittleRepository spittleRepository) {
		this.spittleRepository = spittleRepository;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Spittle> spittles(
			@RequestParam(value="max", defaultValue=MAX_LONG_AS_STRING) long max, 
			@RequestParam(value="count", defaultValue="20") int count) {
		return this.spittleRepository.findSpittles(max, count);
	}
//	public String spittles(Model model) {
//		model.addAttribute("spittleList", this.spittleRepository.findSpittles(Long.MAX_VALUE, 20));
//		return "spittles";
//	}
	
	@RequestMapping(value="/{spittleId}", method=RequestMethod.GET)
	public String spittle(@PathVariable long spittleId, Model model) {
		model.addAttribute(spittleRepository.findOne(spittleId));
		return "spittle";
	}
}
