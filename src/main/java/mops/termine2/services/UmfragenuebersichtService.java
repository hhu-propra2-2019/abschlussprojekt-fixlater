package mops.termine2.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;

@Service
public class UmfragenuebersichtService {
	
	@Autowired
	private UmfrageService umfrageService;
	
	@Autowired
	private GruppeService gruppeService;
	
	private List<Umfrage> getUmfragenVonBenutzer(Account account) {
		List<Umfrage> umfragen = new ArrayList<>();
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		for (Gruppe g : gruppen) {
			umfragen.addAll(umfrageService.loadByGruppeOhneTermine(g.getName()));
		}
		return umfragen;
	}
	
	public List<Umfrage> loadOffeneUmfragenFuerBenutzer(Account account) {
		List<Umfrage> umfragen = getUmfragenVonBenutzer(account);
		List<Umfrage> offeneUmfragen = new ArrayList<>();
		for (Umfrage umfrage : umfragen) {
			if (umfrage.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneUmfragen.add(umfrage);
			}
		}
		return offeneUmfragen;
	}
	
	public List<Umfrage> loadAbgeschlosseneUmfragenFuerBenutzer(Account account) {
		List<Umfrage> umfragen = getUmfragenVonBenutzer(account);
		List<Umfrage> abgeschlosseneUmfragen = new ArrayList<>();
		for (Umfrage umfrage : umfragen) {
			if (umfrage.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneUmfragen.add(umfrage);
			}
		}
		return abgeschlosseneUmfragen;
	}
	
}
