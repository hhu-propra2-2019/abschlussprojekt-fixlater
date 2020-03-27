package mops.termine2.services;

import mops.termine2.database.TerminfindungAntwortRepository;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.models.Terminfindung;
import mops.termine2.util.IntegerToolkit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TerminErgebnisService {
	
	private TerminfindungAntwortRepository antwortRepo;
	
	public TerminErgebnisService(TerminfindungAntwortRepository antwortRepo) {
		this.antwortRepo = antwortRepo;
	}
	
	
	public LocalDateTime berechneErgebnisTerminfindung(Terminfindung terminfindung) {
		List<TerminfindungAntwortDB> terminfindungAntwortDBS =
			antwortRepo.findAllByTerminfindungLink(terminfindung.getLink());
		
		List<LocalDateTime> termine = terminfindung.getVorschlaege();
		
		int[] ja = new int[termine.size()];
		int[] nein = new int[termine.size()];
		int[] vielleicht = new int[termine.size()];
		
		for (TerminfindungAntwortDB terminfindungAntwortDB : terminfindungAntwortDBS) {
			int index = termine.indexOf(terminfindungAntwortDB.getTerminfindung().getTermin());
			switch (terminfindungAntwortDB.getAntwort()) {
			case JA:
				ja[index]++;
				break;
			case NEIN:
				nein[index]++;
				break;
			case VIELLEICHT:
				vielleicht[index]++;
				break;
			default:
				break;
			}
		}
		
		List<Integer> highest = IntegerToolkit.findHighestIndex(ja);
		if (highest.size() > 1) {
			int tmp = highest.get(0);
			for (Integer i : highest) {
				if (vielleicht[tmp] < vielleicht[i]) {
					tmp = i;
				}
				
			}
			highest = new ArrayList<>(Arrays.asList(tmp));
		}
		
		return terminfindung.getVorschlaege().get(highest.get(0));
	}
	
}
