package mops.termine2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/termine2")
public class Termine2Controller {
	
	@GetMapping("")
	String index(Model m) {
		return "termine";
	}
	
	@GetMapping("/termine-abstimmung")
	String index2(Model m) {
		return "termine-abstimmung";
	}
	
	@GetMapping("/termine-neu")
	String terminNeu(Model m) {
		return "termine-neu";
	}
	
	@GetMapping("/umfragen")
	String umfragen(Model m) {
		return "umfragen";
	}
	
	@GetMapping("/umfragen-abstimmung")
	String umfragenAbstimmung(Model m) {
		return "umfragen-abstimmung";
	}
	
	@GetMapping("/umfragen-neu")
	String umfrageNeu(Model m) {
		return "umfragen-neu";
	}
	
}
