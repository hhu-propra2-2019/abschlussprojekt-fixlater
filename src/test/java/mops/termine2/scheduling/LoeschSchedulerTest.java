package mops.termine2.scheduling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mops.termine2.services.KommentarService;
import mops.termine2.services.TerminfindungService;
import mops.termine2.services.UmfrageService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LoeschSchedulerTest {
	
	private transient TerminfindungService terminfindungService;
	
	private transient UmfrageService umfrageService;
	
	private transient KommentarService kommentarService;
	
	private transient LoeschScheduler scheduler;
	
	@BeforeEach
	public void setUp() {
		terminfindungService = mock(TerminfindungService.class);
		umfrageService = mock(UmfrageService.class);
		kommentarService = mock(KommentarService.class);
		
		scheduler = new LoeschScheduler(terminfindungService, umfrageService, kommentarService);
	}
	
	@Test
	public void testeLoeschefunktion() {
		scheduler.loescheDaten();
		
		verify(kommentarService, times(1)).loescheAbgelaufeneKommentareFuerTermine();
		verify(terminfindungService, times(1)).loescheAbgelaufeneTermine();
		verify(kommentarService, times(1)).loescheAbgelaufeneKommentareFuerUmfragen();
		verify(umfrageService, times(1)).loescheAbgelaufeneUmfragen();
	}
	
}
