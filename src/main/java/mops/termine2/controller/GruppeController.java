package mops.termine2.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.BenutzerDTO;
import mops.termine2.models.GruppeDTO;
import mops.termine2.models.GruppenDTO;

@Component
@EnableScheduling
public class GruppeController {
	
	@Autowired
	private BenutzerGruppeRepository repository;
	
	private int statusnummer = 0;
	
	private final String url = "http://localhost:8082/gruppen2/api/updateGroups/{status}";
	
	private RestTemplate rt = new RestTemplate();
	
	private GruppenDTO gruppen;
	
	private List<GruppeDTO> gruppeListe;
	
	@Scheduled(fixedDelay = 30000)
	public void updateGruppe() {
		ResponseEntity<GruppenDTO> result;
		try {
			result = rt.getForEntity(url, GruppenDTO.class, statusnummer);
		} catch (HttpClientErrorException e) {
			return;
		}
		
		gruppen = result.getBody();
		
		try {
			gruppeListe = gruppen.getGroupList();
			statusnummer = gruppen.getStatus();
		} catch (NullPointerException e) {
			return;
		}
		
		for (GruppeDTO gruppeDTO : gruppeListe) {
			String gruppe = gruppeDTO.getTitle();
			Long gruppeId = Integer.toUnsignedLong(gruppeDTO.getId());
			if (!gruppe.equals("null")) {
				List<BenutzerDTO> members = gruppeDTO.getMembers();
				List<String> aktuelleBenutzer = repository.findBenutzerByGruppeId(gruppeId);
				List<String> neueBenutzer = new ArrayList<>();
				
				for (BenutzerDTO member : members) {
					String benutzername = member.getUser_id();
					if (!aktuelleBenutzer.contains(benutzername)) {
						neueBenutzer.add(benutzername);
					} else {
						aktuelleBenutzer.remove(benutzername);
					}
				}
				
				benutzerHinzufuegen(neueBenutzer, gruppe, gruppeId);
				
				benutzerLoeschen(aktuelleBenutzer, gruppeId);
				
			} else {
				repository.deleteAllByGruppeId(gruppeId);
			}
		}
	}
	
	private void benutzerHinzufuegen(List<String> neueBenutzer, String gruppe, Long gruppeId) {
		for (String benutzer : neueBenutzer) {
			BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
			benutzerGruppeDB.setBenutzer(benutzer);
			benutzerGruppeDB.setGruppe(gruppe);
			benutzerGruppeDB.setGruppeId(gruppeId);
			repository.save(benutzerGruppeDB);
		}
	}
	
	private void benutzerLoeschen(List<String> benutzerliste, Long gruppeId) {
		for (String benutzer : benutzerliste) {
			repository.deleteByBenutzerAndGruppeId(benutzer, gruppeId);
		}
	}
	
}
