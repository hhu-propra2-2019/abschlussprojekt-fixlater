package mops.termine2.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import mops.termine2.services.TerminfindungService;
import mops.termine2.services.UmfrageService;

@Component
@EnableScheduling
public class LoeschScheduler {
	
	private TerminfindungService terminfindungService;
	
	private UmfrageService umfrageService;
	
	@Autowired
	public LoeschScheduler(TerminfindungService terminfindungService, UmfrageService umfrageService) {
		this.terminfindungService = terminfindungService;
		this.umfrageService = umfrageService;
	}
	
	@Scheduled(fixedDelay = 3000)
	public void loescheDaten() {
		terminfindungService.loescheAbgelaufene();
		umfrageService.deleteOutdated();
	}
}
