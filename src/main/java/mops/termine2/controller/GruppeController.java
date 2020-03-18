package mops.termine2.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.BenutzerDTO;
import mops.termine2.models.GruppeDTO;
import mops.termine2.models.GruppenDTO;

@Controller
@EnableScheduling
public class GruppeController {
	
	private BenutzerGruppeRepository repository;
	
	private int statusnummer = 0;
	
	private final String url = "http://localhost:8082/gruppen2/api/updateGroups/{status}";
	
	private RestTemplate rt = new RestTemplate();
	
	private GruppenDTO gruppen;
	
	private List<GruppeDTO> gruppeListe;
	
	@Scheduled(fixedRate = 30000)
	public void updateGruppe() {
		System.out.println("Starte Update");
		ResponseEntity<GruppenDTO> result;
		try {
			result = rt.getForEntity(url, GruppenDTO.class, statusnummer);
		} catch (HttpClientErrorException e) {
			System.out.println("Fehler");
			return;
		}
		
		gruppen = result.getBody();
		System.out.println(gruppen);
		
		try {
			gruppeListe = gruppen.getGroupList();
			statusnummer = gruppen.getStatus();
		} catch (NullPointerException e) {
			return;
		}
		
		for (GruppeDTO gruppe : gruppeListe) {
			if (!gruppe.getTitle().equals("null")) {
				System.out.println(gruppe.getTitle());
				for (BenutzerDTO benutzerDTO : gruppe.getMembers()) {
					BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
					benutzerGruppeDB.setBenutzer(benutzerDTO.getUser_id());
					benutzerGruppeDB.setGruppe(gruppe.getTitle());
					benutzerGruppeDB.setGruppeId(Integer.toUnsignedLong(gruppe.getId()));
					// repository.save(benutzerGruppeDB);
				}
			} else {
				System.out.println(gruppe.getTitle());
			}
		}
	}
	
}
