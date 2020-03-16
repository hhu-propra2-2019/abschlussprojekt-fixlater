package mops.termine2.controller;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.termine2.Konstanten;
import mops.termine2.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@Controller
@SessionScope
@RequestMapping("/termine2")
public class UmfragenAbstimmungController {
	
	private final transient Counter authenticatedAccess;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	public UmfragenAbstimmungController(MeterRegistry registry) {
		authenticatedAccess = registry.counter("access.authenticated");
	}
	
	@GetMapping("/umfragen-abstimmung")
	@RolesAllowed({Konstanten.ROLE_ORGA, Konstanten.ROLE_STUDENTIN})
	public String umfragenAbstimmung(Principal p, Model m) {
		if (p != null) {
			m.addAttribute(Konstanten.ACCOUNT, authenticationService.createAccountFromPrincipal(p));
		}
		authenticatedAccess.increment();
		
		return "umfragen-abstimmung";
	}
}
