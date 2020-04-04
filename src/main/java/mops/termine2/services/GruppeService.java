package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.Gruppe;
import mops.termine2.models.Terminfindung;
import mops.termine2.models.Umfrage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bietet Methoden im Umgang mit Gruppen an und
 * bildet die dementsprechende Schnittstelle zwischen 
 * Controllern und Datenbank
 */
@Service
public class GruppeService {
	
	private transient BenutzerGruppeRepository benutzerGruppeRepository;
	
	public GruppeService(BenutzerGruppeRepository benutzerGruppeRepository) {
		this.benutzerGruppeRepository = benutzerGruppeRepository;
	}
	
	/**
	 * Holt die Gruppen aus der Datenbank, in denen der übergebene Benutzer
	 * Mitglied ist.
	 * 
	 * @param account Account Objekt des Benutzers
	 * 
	 * @return Liste von Gruppen
	 */
	public List<Gruppe> loadByBenutzer(Account account) {
		List<BenutzerGruppeDB> gruppenDB = benutzerGruppeRepository.findByBenutzer(account.getName());
		List<Gruppe> gruppen = new ArrayList<>();
		
		for (BenutzerGruppeDB gruppeDB : gruppenDB) {
			Gruppe g = new Gruppe();
			g.setId(gruppeDB.getGruppeId());
			g.setName(gruppeDB.getGruppe());
			gruppen.add(g);
		}
		
		return gruppen;
	}
	
	/**
	 * Holt die Gruppe aus der Datenbank, die zu {@code id} passt.
	 *
	 * @param id Eindeutige Id der gewünschten Gruppe
	 * 
	 * @return Gruppe, wenn ID in der Datenbank vorhanden ist, oder null, wenn nicht.
	 */
	public Gruppe loadByGruppeId(String id) {
		List<BenutzerGruppeDB> gruppeDB = benutzerGruppeRepository.findByGruppeId(id);
		
		if (gruppeDB.size() > 0) {
			Gruppe gruppe = new Gruppe();
			gruppe.setName(gruppeDB.get(0).getGruppe());
			gruppe.setId(gruppeDB.get(0).getGruppeId());
			return gruppe;
		}
		
		return null;
	}
	
	/**
	 * Sucht Gruppe mit Gruppen-ID {@code id} und gibt diese zurück, falls vorhanden
	 * 
	 * @param id Gruppen-ID der gewünschten Gruppe
	 * 
	 * @return Gruppe mit Gruppen-ID {@code id} falls vorhanden, ansonsten wird 
	 * {@link #erstelleStandardGruppe} verwendet
	 */
	public Gruppe loadByGruppeIdOderStandard(String id) {
		Gruppe gruppe = loadByGruppeId(id);
		if (gruppe == null) {
			return erstelleStandardGruppe();
		}		
		return gruppe;
	}
	
	/**
	 * Überprüft, ob der übergebene {@code account} Mitglied der Gruppe mit
	 * Gruppen-ID {@code gruppeId} ist
	 * 
	 * @param account Account Objekt des Benutzers
	 * @param gruppeId Gruppen-ID der Gruppe
	 * 
	 * @return {@code true}, falls der Benutzer Mitglied in der Gruppe ist, {@code false} sonst
	 */
	public boolean pruefeAccountInGruppe(Account account, String gruppeId) {
		String benutzer = account.getName();
		return benutzerGruppeRepository.findByBenutzerAndGruppeId(benutzer, gruppeId) != null;
	}
	
	/**
	 * Sortiert eine Liste von Gruppen
	 * 
	 * @param gruppen Liste von Gruppen, die sortiert werden sollen
	 * 
	 * @return Neue Liste von Gruppen nach Gruppennamen alphabetisch sortiert
	 */
	public List<Gruppe> sortiereGruppenNachNamen(List<Gruppe> gruppen) {
		return gruppen.stream()
			.sorted(Comparator.comparing(Gruppe::getName))
			.collect(Collectors.toList());
	}
	
	/**
	 * Sucht die Gruppen des Nutzers in der Datenbank und gibt sie sortiert zurück
	 * 
	 * @param account Account Objekt des Benutzers
	 * 
	 * @return Liste der Gruppen des Benutzers nach Gruppennamen alphabetisch sortiert
	 */
	public List<Gruppe> loadByBenutzerSortiert(Account account) {
		List<Gruppe> gruppen = loadByBenutzer(account);
		return sortiereGruppenNachNamen(gruppen);
	}
	
	/**
	 * Überprüft, ob der Nutzer des {@code account}s Zugriff auf die Gruppe
	 * mit Gruppenid {@code id} hat
	 * 
	 * @param account Das Account Objekt des Nutzers
	 * @param id Die Id der Gruppe, deren Zugriff gewünscht ist
	 * 
	 * @return {@code true}, wenn der Zugriff verweigert werden soll, {@code false} sonst.
	 * 		   {@code false} wird auch zurückgegeben, wenn {@code id} {@code null}
	 * 		   oder "-1" ist.
	 */
	public boolean pruefeGruppenzugriffVerweigert(Account account, String id) {
		return id != null && !id.contentEquals("-1") && !pruefeAccountInGruppe(account, id);
	}
	
	/**
	 * Erstellt ein Gruppen Objekt
	 * 
	 * @return Gruppen Objekt mit Id "-1" und Name "Alle Gruppen"
	 */
	public Gruppe erstelleStandardGruppe() {
		Gruppe gruppe = new Gruppe();
		gruppe.setId("-1");
		gruppe.setName("Alle Gruppen");
		return gruppe;
	}
	
	/**
	 * Überprüft die übergebene Gruppe und setzt bei Erfolg die Gruppen Id
	 * der Terminfindung auf die Id der übergebenen Gruppe.
	 * 
	 * @param terminfindung Terminfindung deren Gruppen id gesetzt werden soll
	 * @param gruppeSelektiert Gruppe deren Id übernommen werden soll
	 */
	public void setzeGruppeId(Terminfindung terminfindung, Gruppe gruppeSelektiert) {
		if (sollIdSetzen(gruppeSelektiert)) {
			terminfindung.setGruppeId(gruppeSelektiert.getId());
		}
	}
	
	/**
	 * Überprüft die übergebene Gruppe und setzt bei Erfolg die Gruppen Id
	 * der Umfrage auf die Id der übergebenen Gruppe.
	 * 
	 * @param umfrage Umfrage deren Gruppen id gesetzt werden soll
	 * @param gruppeSelektiert Gruppe deren Id übernommen werden soll
	 */
	public void setzeGruppeId(Umfrage umfrage, Gruppe gruppeSelektiert) {
		if (sollIdSetzen(gruppeSelektiert)) {
			umfrage.setGruppeId(gruppeSelektiert.getId());
		}		
	}
	
	/**
	 * Extrahiert die Gruppenid und den Namen aus den übergebenen Gruppen
	 * 
	 * @param gruppen Die Gruppen deren Werte extrahiert werden sollen
	 * 
	 * @return Eine HashMap mit den Gruppenids als Schlüssel und den Namen als Werte
	 */
	public HashMap<String, String> extrahiereIdUndNameAusGruppen(List<Gruppe> gruppen) {
		HashMap<String, String> groups = new HashMap<>();
		for (Gruppe group : gruppen) {
			groups.put(group.getId(), group.getName());
		}
		return groups;
	}
	
	private boolean sollIdSetzen(Gruppe gruppe) {
		if (gruppe != null && gruppe.getId() != null) {
			Gruppe gruppeAusDB = loadByGruppeId(gruppe.getId());
			if (gruppeAusDB != null) {
				return true;				
			}
		}
		return false;
	}
	
}
