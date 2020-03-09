package mops.termine2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
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
	
	@GetMapping("/termin-neu")
	String terminNeu(Model m) {
		return "termin-neu";
	}
	
	@GetMapping("/umfragen-abstimmung")
	String umfragenAbstimmung(Model m) {
		return "umfragen-abstimmung";
	}
	
	@GetMapping("/umfrage-neu")
	String umfrageNeu(Model m) {
		return "umfrage-neu";
	}
	
}
