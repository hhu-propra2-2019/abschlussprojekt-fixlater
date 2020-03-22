package mops.termine2.services;

import mops.termine2.authentication.Account;
import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.Gruppe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GruppeService {
	
	private transient BenutzerGruppeRepository benutzerGruppeRepository;
	
	public GruppeService(BenutzerGruppeRepository benutzerGruppeRepository) {
		this.benutzerGruppeRepository = benutzerGruppeRepository;
	}
	
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
	
	public Gruppe loadByGruppeId(Long id) {
		List<BenutzerGruppeDB> gruppeDB = benutzerGruppeRepository.findByGruppeId(id);
		
		if (gruppeDB.size() > 0) {
			Gruppe gruppe = new Gruppe();
			gruppe.setName(gruppeDB.get(0).getGruppe());
			gruppe.setId(gruppeDB.get(0).getGruppeId());
			return gruppe;
		}
		
		return null;
	}
	
	public boolean accountInGruppe(Account account, Long gruppeId) {
		String benutzer = account.getName();
		System.out.println("Ja bis hier ist man gekommen");
		return !benutzerGruppeRepository.findByBenutzerAndGruppeId(benutzer, gruppeId).isEmpty();
	}
	
}
