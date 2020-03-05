package mops.termine2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Termine2Controller {
	@GetMapping("/")
	String index(Model m) {
		return "index";
	}

	@GetMapping("/termin-neu")
	String terminNeu(Model m) {
		return "termin-neu";
	}	
	
}
