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
	String termineAbstimmung(Model m) {
		return "termine-abstimmung";
	}
	
	@GetMapping("/termine-neu")
	String termineNeu(Model m) {
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
	String umfragenNeu(Model m) {
		return "umfragen-neu";
	}
	
}
