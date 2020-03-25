package mops.termine2.scheduling;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import mops.termine2.services.TerminfindungService;
import mops.termine2.services.UmfrageService;

@Component
@EnableScheduling
public class LoeschScheduler {
	
	private final Logger logger = Logger.getLogger(LoeschScheduler.class.getName());
	
	private TerminfindungService terminfindungService;
	
	private UmfrageService umfrageService;
	
	@Autowired
	public LoeschScheduler(TerminfindungService terminfindungService, UmfrageService umfrageService) {
		this.terminfindungService = terminfindungService;
		this.umfrageService = umfrageService;
	}
	
	@Scheduled(cron = "0 0 0,12 * * *")
	public void loescheDaten() {
		logger.info("Loesche abgelaufene Daten");
		terminfindungService.loescheAbgelaufene();
		umfrageService.deleteOutdated();
	}
}
