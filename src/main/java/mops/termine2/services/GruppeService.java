package mops.termine2.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import mops.termine2.authentication.Account;
import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.Gruppe;

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
			g.setId(gruppeDB.getId());
			g.setName(gruppeDB.getGruppe());
			gruppen.add(g);
		}
		
		return gruppen;
	}
	
}
