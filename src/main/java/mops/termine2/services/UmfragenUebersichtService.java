package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Umfrage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bietet Methoden zur Filterung von Umfragen bezüglich der Frist
 * für einzelne benutzer oder ganze Gruppen
 */
@Service
public class UmfragenUebersichtService {
	
	@Autowired
	private UmfrageService umfrageService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private UmfrageAntwortService umfrageAntwortService;
	
	public UmfragenUebersichtService(UmfrageService umfrageService,
									 GruppeService gruppeService,
									 UmfrageAntwortService umfrageAntwortService) {
		this.umfrageService = umfrageService;
		this.gruppeService = gruppeService;
		this.umfrageAntwortService = umfrageAntwortService;
	}
	
	/**
	 * Holt alle noch offenen Umfragen zu der Gruppe mit 
	 * Gruppen-ID {@code gruppeId} aus der Datenbank. 
	 * Ist {@code gruppe} {@code null} oder hat ID "-1", so werden
	 * die offenen Umfragen für den Benutzer aus der Datenbank geholt
	 * ohne Filterung nach einer Gruppe.
	 * Eine offene Umfrage ist dabei eine Umfrage, deren Frist
	 * in der Zukunft liegt.
	 *
	 * @param account Das Account Objekt des aktuellen Benutzers
	 * @param gruppe Das Gruppen Objekt der ausgewählten Gruppe
	 * 
	 * @return die noch offenen Umfragen für die Gruppe oder den Benutzer
	 */
	public List<Umfrage> loadOffeneUmfragen(Account account, Gruppe gruppe) {
		if (gruppe == null || gruppe.getId().equals("-1")) {
			return loadOffeneUmfragenFuerBenutzer(account);
		}
		return loadOffeneUmfragenFuerGruppe(account, gruppe.getId());
	}
	
	/**
	 * Holt alle schon abgeschlossenen Umfragen zu der Gruppe mit 
	 * Gruppen-ID {@code gruppeId} aus der Datenbank. 
	 * Ist {@code gruppe} {@code null} oder hat ID "-1", so werden
	 * die abgeschlossenen Umfragen für den Benutzer aus der Datenbank geholt
	 * ohne Filterung nach einer Gruppe.
	 * Eine abgeschlossene Umfrage ist dabei eine Umfrage, deren Frist
	 * in der Vergangenheit liegt.
	 *
	 * @param account Das Account Objekt des aktuellen Benutzers
	 * @param gruppe Das Gruppen Objekt der ausgewählten Gruppe
	 * 
	 * @return die bereits abgeschlossenen Umfragen für die Gruppe oder den Benutzer
	 */
	public List<Umfrage> loadAbgeschlosseneUmfragen(Account account, Gruppe gruppe) {
		if (gruppe == null || gruppe.getId().equals("-1")) {
			return loadAbgeschlosseneUmfragenFuerBenutzer(account);
		}
		return loadAbgeschlosseneUmfragenFuerGruppe(account, gruppe.getId());
	}
	
	private List<Umfrage> loadOffeneUmfragenFuerGruppe(Account account, String gruppeId) {
		List<Umfrage> umfragen = new ArrayList<>();
		umfragen.addAll(umfrageService.loadByGruppeOhneVorschlaege(gruppeId));
		List<Umfrage> offeneUmfragen = filtereOffeneUmfragen(umfragen);
		
		offeneUmfragen = offeneUmfragen.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		offeneUmfragen = setTeilgenommen(offeneUmfragen, account);
		
		return offeneUmfragen;
	}
	
	private List<Umfrage> loadAbgeschlosseneUmfragenFuerGruppe(Account account, String gruppeId) {
		List<Umfrage> umfragen = new ArrayList<>();
		umfragen.addAll(umfrageService.loadByGruppeOhneVorschlaege(gruppeId));
		List<Umfrage> abgeschlosseneUmfragen = filtereAbgeschlosseneUmfragen(umfragen);
		
		abgeschlosseneUmfragen = sortiereUmfragenNachFrist(abgeschlosseneUmfragen);
		
		return abgeschlosseneUmfragen;
	}
	
	private List<Umfrage> loadOffeneUmfragenFuerBenutzer(Account account) {
		List<Umfrage> umfragen = getAllUmfragenVonBenutzer(account);
		List<Umfrage> offeneUmfragen = filtereOffeneUmfragen(umfragen);
		
		offeneUmfragen = offeneUmfragen.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		offeneUmfragen = setTeilgenommen(offeneUmfragen, account);
		
		return offeneUmfragen;
	}
	
	private List<Umfrage> loadAbgeschlosseneUmfragenFuerBenutzer(Account account) {
		List<Umfrage> umfragen = getAllUmfragenVonBenutzer(account);
		List<Umfrage> abgeschlosseneUmfragen = filtereAbgeschlosseneUmfragen(umfragen);
		
		abgeschlosseneUmfragen = sortiereUmfragenNachFrist(abgeschlosseneUmfragen);
		
		return abgeschlosseneUmfragen;
	}
	
	private List<Umfrage> getUmfragenVonBenutzer(Account account) {
		List<Umfrage> umfragen = new ArrayList<>();
		umfragen.addAll(umfrageService.loadByErstellerOhneVorschlaege(account.getName()));
		
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		for (Gruppe g : gruppen) {
			umfragen.addAll(umfrageService.loadByGruppeOhneVorschlaege(g.getId()));
		}
		return umfragen;
	}
	
	private List<Umfrage> filtereOffeneUmfragen(List<Umfrage> umfragen) {
		List<Umfrage> offeneUmfragen = new ArrayList<>();
		for (Umfrage umfrage : umfragen) {
			if (umfrage.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneUmfragen.add(umfrage);
			}
		}
		return offeneUmfragen;
	}
	
	private List<Umfrage> filtereAbgeschlosseneUmfragen(List<Umfrage> umfragen) {
		List<Umfrage> abgeschlosseneUmfragen = new ArrayList<>();
		for (Umfrage umfrage : umfragen) {
			if (umfrage.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				abgeschlosseneUmfragen.add(umfrage);
			}
		}
		return abgeschlosseneUmfragen;
	}
	
	private List<Umfrage> setTeilgenommen(List<Umfrage> umfragen, Account account) {
		for (Umfrage umfrage : umfragen) {
			umfrage.setTeilgenommen(umfrageAntwortService
				.hatNutzerAbgestimmt(account.getName(), umfrage.getLink()));
		}
		return umfragen;
	}
	
	private List<Umfrage> sortiereUmfragenNachFrist(List<Umfrage> umfragen) {
		
		List<Umfrage> umfragenSortiert = umfragen.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		return umfragenSortiert;
	}
	
	private List<Umfrage> getAllUmfragenVonBenutzer(Account account) {
		List<Umfrage> umfragen = getUmfragenVonBenutzer(account);
		umfragen.addAll(umfrageService.loadAllBenutzerHatAbgestimmtOhneVorschlaege(account.getName()));
		
		umfragen = eindeutigeUmfragenListe(umfragen);
		
		return umfragen;
	}
	
	private List<Umfrage> eindeutigeUmfragenListe(List<Umfrage> umfragen) {
		List<Umfrage> distinct = new ArrayList<>();
		List<String> links = new ArrayList<>();
		
		for (Umfrage umfrage : umfragen) {
			if (!links.contains(umfrage.getLink())) {
				links.add(umfrage.getLink());
				distinct.add(umfrage);
			}
		}		
		return distinct;
	}
}

