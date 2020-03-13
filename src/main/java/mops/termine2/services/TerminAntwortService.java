package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;

public class TerminAntwortService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	private TerminfindungService terminfindungService;
	
	public TerminAntwortService(TerminfindungAntwortRepository terminfindungAntwortRepository,
								TerminfindungService terminFService) {
		this.terminfindungService = terminFService;
		antwortRepo = terminfindungAntwortRepository;
	}
}
