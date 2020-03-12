package mops.termine2.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;

@Service
public class TerminfindunguebersichtService {
	
	@Autowired
	private TerminfindungService terminfindungService;
	
	@Autowired
	private GruppeService gruppeService;
	
	public List<Terminfindung> loadOffeneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = new ArrayList<>();
		List<Terminfindung> offeneTermine = new ArrayList<>();
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		
		for (Gruppe g : gruppen) {
			termine.addAll(terminfindungService.loadByGruppeOhneTermine(g.getName()));
		}
		
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneTermine.add(termin);
			}
		}
		
		return offeneTermine;
	}
	
	public List<Terminfindung> loadAbgeschlosseneTerminfindungenFuerBenutzer(Account account) {
		List<Terminfindung> termine = new ArrayList<>();
		List<Terminfindung> abgeschlosseneTermine = new ArrayList<>();
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		
		for (Gruppe g : gruppen) {
			termine.addAll(terminfindungService.loadByGruppeOhneTermine(g.getName()));
		}
		
		for (Terminfindung termin : termine) {
			if (termin.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneTermine.add(termin);
			}
		}
		
		return abgeschlosseneTermine;
	}
	
}
