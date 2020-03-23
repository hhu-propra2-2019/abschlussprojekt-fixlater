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

@Service
public class UmfragenuebersichtService {
	
	@Autowired
	private UmfrageService umfrageService;
	
	@Autowired
	private GruppeService gruppeService;
	
	@Autowired
	private UmfrageAntwortService umfrageAntwortService;
	
	public UmfragenuebersichtService(UmfrageService umfrageService,
									 GruppeService gruppeService, UmfrageAntwortService umfrageAntwortService) {
		this.umfrageService = umfrageService;
		this.gruppeService = gruppeService;
		this.umfrageAntwortService = umfrageAntwortService;
	}
	
	/**
	 * Geht die Umfragen durch und filtert nach offenen die zu einer Gruppe gehören
	 *
	 * @param account
	 * @param gruppe
	 * @return eine Liste von offenen Umfragen nach Gruppe
	 */
	public List<Umfrage> loadOffeneUmfragenFuerGruppe(Account account, String gruppe) {
		List<Umfrage> umfragen = new ArrayList<>();
		umfragen.addAll(umfrageService.loadByGruppeOhneUmfragen(gruppe));
		List<Umfrage> offeneUmfragen = filterOpenSurveys(umfragen);
		
		offeneUmfragen = offeneUmfragen.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		offeneUmfragen = setTeilgenommen(offeneUmfragen, account);
		
		return offeneUmfragen;
	}
	
	/**
	 * Geht die Umfragen durch und filtert nach abgeschlossenen die zu einer Gruppe gehören
	 *
	 * @param account
	 * @param gruppe
	 * @return eine Liste von abgeschlossenen Umfragen nach Gruppe
	 */
	public List<Umfrage> loadAbgeschlosseneUmfragenFuerGruppe(Account account, String gruppe) {
		List<Umfrage> umfragen = new ArrayList<>();
		umfragen.addAll(umfrageService.loadByGruppeOhneUmfragen(gruppe));
		List<Umfrage> abgeschlosseneUmfragen = filterClosedSurveys(umfragen);
		
		abgeschlosseneUmfragen = sortiereAbgeschlosseneUmfragen(abgeschlosseneUmfragen);
		
		return abgeschlosseneUmfragen;
	}
	
	/**
	 * Geht die Umfragen durch und filtert nach offenen die zu einem Nutzer gehören
	 *
	 * @param account
	 * @return eine Liste von offenen Umfragen nach Nutzer
	 */
	public List<Umfrage> loadOffeneUmfragenFuerBenutzer(Account account) {
		List<Umfrage> umfragen = getAllUmfragenVonBenutzer(account);
		List<Umfrage> offeneUmfragen = filterOpenSurveys(umfragen);
		
		offeneUmfragen = offeneUmfragen.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		offeneUmfragen = setTeilgenommen(offeneUmfragen, account);
		
		return offeneUmfragen;
	}
	
	
	/**
	 * Geht die Umfragen durch und filtert nach abgeschlossenen die zu einem Nutzer gehören
	 *
	 * @param account
	 * @return eine Liste von abgeschlossenen Umfragen nach Nutzer
	 */
	public List<Umfrage> loadAbgeschlosseneUmfragenFuerBenutzer(Account account) {
		List<Umfrage> umfragen = getAllUmfragenVonBenutzer(account);
		List<Umfrage> abgeschlosseneUmfragen = filterClosedSurveys(umfragen);
		
		abgeschlosseneUmfragen = sortiereAbgeschlosseneUmfragen(abgeschlosseneUmfragen);
		
		return abgeschlosseneUmfragen;
	}
	
	private List<Umfrage> getUmfragenVonBenutzer(Account account) {
		List<Umfrage> umfragen = new ArrayList<>();
		List<Gruppe> gruppen = gruppeService.loadByBenutzer(account);
		for (Gruppe g : gruppen) {
			umfragen.addAll(umfrageService.loadByGruppeOhneUmfragen(g.getName()));
		}
		return umfragen;
	}
	
	private List<Umfrage> filterOpenSurveys(List<Umfrage> umfragen) {
		List<Umfrage> offeneUmfragen = new ArrayList<>();
		for (Umfrage umfrage : umfragen) {
			if (umfrage.getFrist().compareTo(LocalDateTime.now()) > 0) {
				offeneUmfragen.add(umfrage);
			}
		}
		return offeneUmfragen;
	}
	
	private List<Umfrage> filterClosedSurveys(List<Umfrage> umfragen) {
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
	
	private List<Umfrage> sortiereAbgeschlosseneUmfragen(List<Umfrage> umfragen) {
		List<Umfrage> umfragenInVergangenheit = new ArrayList<>();
		List<Umfrage> umfragenInZukunft = new ArrayList<>();
		
		for (Umfrage umfrage : umfragen) {
			if (umfrage.getFrist().compareTo(LocalDateTime.now()) <= 0) {
				umfragenInVergangenheit.add(umfrage);
			} else {
				umfragenInZukunft.add(umfrage);
			}
		}
		
		umfragenInVergangenheit = umfragenInVergangenheit.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		umfragenInZukunft = umfragenInZukunft.stream()
			.sorted(Comparator.comparing(Umfrage::getFrist))
			.collect(Collectors.toList());
		
		List<Umfrage> umfragenSortiert = new ArrayList<>();
		umfragenSortiert.addAll(umfragenInVergangenheit);
		umfragenSortiert.addAll(umfragenInZukunft);
		
		return umfragenSortiert;
	}
	
	private List<Umfrage> getAllUmfragenVonBenutzer(Account account) {
		List<Umfrage> umfragen = getUmfragenVonBenutzer(account);
		umfragen.addAll(umfrageService.loadAllBenutzerHatAbgestimmtOhneUmfrage(account.getName()));
		
		umfragen = distinct(umfragen);
		
		return umfragen;
	}
	
	private List<Umfrage> distinct(List<Umfrage> umfragen) {
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

