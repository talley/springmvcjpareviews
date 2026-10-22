package com.talleyouro.springmvcjpademo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class HomeController {

	private final ShipperRepository shipperRepository;
	
	 public HomeController(ShipperRepository shipperRepository) {
	        this.shipperRepository = shipperRepository;
	    }
	 
	@GetMapping("/")
	public String Index(Model model) {
		var shippers=this.shipperRepository.findAll();
		model.addAttribute("shippers", shippers);
		return "index";
	}
	
	@GetMapping("/edit/{shipperId}")
	public String editShipper(Model model, @PathVariable("shipperId") Short shipperId) {

	    var shipper = this.shipperRepository.findById(shipperId);
	    if (shipper.isEmpty()) {
	        return "redirect:/";          // id not found, go back to the list
	    }
	    model.addAttribute("shipper", shipper.get());
	    return "EditShipper";
	}

	@PostMapping("/edit")
	public String saveShipper(@ModelAttribute("shipper") Shipper form) {

	    var existing = this.shipperRepository.findById(form.getShipperId());
	    if (existing.isPresent()) {
	        Shipper s = existing.get();
	        s.setCompanyName(form.getCompanyName());
	        s.setPhone(form.getPhone());
	        s.setEditedBy(form.getEditedBy());
	        s.setEditedOn(java.time.LocalDate.now());
	        this.shipperRepository.save(s);
	    }
	    return "redirect:/";
	}
	
	// creating new shipper
	@GetMapping("/add")
	public String addShipper(Model model) {
	    model.addAttribute("shipper", new Shipper());
	    return "AddShipper";
	}

	@PostMapping("/add")
	public String saveNewShipper(@ModelAttribute("shipper") Shipper form) {
	    form.setShipperId(null);
	    form.setAddedOn(java.time.LocalDate.now());
	    this.shipperRepository.save(form);
	    return "redirect:/";
	}
}
