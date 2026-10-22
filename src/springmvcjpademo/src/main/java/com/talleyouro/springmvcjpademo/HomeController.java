package com.talleyouro.springmvcjpademo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {

	private final ShipperRepository shipperRepository;
	
	 public HomeController(ShipperRepository shipperRepository) {
	        this.shipperRepository = shipperRepository;
	    }
	 
	@GetMapping("/")
	public String Index(Model data) {
		var shippers=this.shipperRepository.findAll();
		data.addAttribute("shippers", shippers);
		return "index";
	}
}
