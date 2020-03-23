package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.Gruppe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GruppeService {
	
	private transient BenutzerGruppeRepository benutzerGruppeRepository;
	
	public GruppeService(BenutzerGruppeRepository benutzerGruppeRepository) {
		this.benutzerGruppeRepository = benutzerGruppeRepository;
	}
	
	/**
	 * Bekommt den Account übergeben und lädt die zugehörigen Gruppen
	 * @param account
	 * @return Liste von Gruppen
	 */
	public List<Gruppe> loadByBenutzer(Account account) {
		List<BenutzerGruppeDB> gruppenDB = benutzerGruppeRepository.findByBenutzer(account.getName());
		List<Gruppe> gruppen = new ArrayList<>();
		
		for (BenutzerGruppeDB gruppeDB : gruppenDB) {
			Gruppe g = new Gruppe();
			g.setId(gruppeDB.getId());
			g.setName(gruppeDB.getGruppe());
			gruppen.add(g);
		}
		
		return gruppen;
	}
	
	/**
	 * Lädt Gruppe nach Id
	 * @param id
	 * @return
	 */
	public Gruppe loadById(Long id) {
		Optional gruppeDB = benutzerGruppeRepository.findById(id);
		
		if (gruppeDB.isPresent()) {
			BenutzerGruppeDB gruppeDB1 = (BenutzerGruppeDB) gruppeDB.get();
			Gruppe gruppe = new Gruppe();
			gruppe.setName(gruppeDB1.getGruppe());
			gruppe.setId(gruppeDB1.getId());
			return gruppe;
		}
		
		return null;
	}
	
	public boolean accountInGruppe(Account account, String gruppe) {
		String benutzer = account.getName();
		return !benutzerGruppeRepository.findByBenutzerAndGruppe(benutzer, gruppe).isEmpty();
	}
	
}
