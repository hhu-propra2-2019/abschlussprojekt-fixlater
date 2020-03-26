package mops.termine2.scheduling;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.services.KommentarService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.services.UmfrageService;

@Component
@EnableScheduling
public class LoeschScheduler {
	
	private final Logger logger = Logger.getLogger(LoeschScheduler.class.getName());
	
	private TerminfindungService terminfindungService;
	
	private UmfrageService umfrageService;
	
	private KommentarService kommentarService;
	
	@Autowired
	public LoeschScheduler(TerminfindungService terminfindungService, UmfrageService umfrageService,
		KommentarService kommentarService) {
		this.terminfindungService = terminfindungService;
		this.umfrageService = umfrageService;
		this.kommentarService = kommentarService;
	}
	
	@Scheduled(cron = "0 0 0,12 * * *")
	public void loescheDaten() {
		logger.info("Lösche abgelaufene Daten");
		loescheTermine();
		loescheUmfragen();
	}
	
	@Transactional
	public void loescheTermine() {
		kommentarService.loescheAbgelaufeneKommentareFuerTermine();
		terminfindungService.loescheAbgelaufeneTermine();
	}
	
	@Transactional
	public void loescheUmfragen() {
		kommentarService.loescheAbgelaufeneKommentareFuerUmfragen();
		umfrageService.loescheAbgelaufeneUmfragen();
	}
}
