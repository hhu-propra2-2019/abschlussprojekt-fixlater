package mops.termine2.scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import mops.termine2.database.BenutzerGruppeRepository;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.models.BenutzerDTO;
import mops.termine2.models.GruppeDTO;
import mops.termine2.models.GruppenDTO;

@Component
@EnableScheduling
public class GruppeScheduler {
	
	private final Logger logger = Logger.getLogger(GruppeScheduler.class.getName());
	
	private BenutzerGruppeRepository repository;
	
	private int statusnummer = 0;
	
	@Value("${termine2.gruppescheduler.url}")
	private String urlPrefix = "http://localhost:8082";
	
	private RestTemplate template;
	
	private GruppenDTO gruppen;
	
	private List<GruppeDTO> gruppeListe;
	
	@Autowired
	public GruppeScheduler(BenutzerGruppeRepository repo, RestTemplate rt) {
		repository = repo;
		template = rt;
	}
	
	@Scheduled(fixedDelay = 30000)
	public void updateGruppe() {
		String url = urlPrefix + "/gruppen2/api/updateGroups/{status}";
		logger.info("Hole Gruppenupdate von " + url.replace("{status}", Integer.toString(statusnummer)));
		ResponseEntity<GruppenDTO> result;
		try {
			result = template.getForEntity(url, GruppenDTO.class, statusnummer);
		} catch (HttpClientErrorException e) {
			logger.warning("Anfrage nicht erfolgreich: HttpClientErrorException");
			return;
		} catch (ResourceAccessException e) {
			logger.warning("Anfrage nicht erfolgreich: ResourceAccessException");
			return;
		}
		
		gruppen = result.getBody();
		
		try {
			gruppeListe = gruppen.getGroupList();
			statusnummer = gruppen.getStatus();
		} catch (NullPointerException e) {
			logger.warning("Update nicht erfolgreich: NullPointerException");
			return;
		}
		
		for (GruppeDTO gruppeDTO : gruppeListe) {
			String gruppe = gruppeDTO.getTitle();
			String gruppeId = gruppeDTO.getId();
			if (!gruppe.equals("null")) {
				Optional<String> aktuellerGruppentitel = repository.findGruppeByGruppeId(gruppeId);
				if (aktuellerGruppentitel.isPresent() && !aktuellerGruppentitel.get().equals(gruppe)) {
					repository.deleteAllByGruppeId(gruppeId);
				}
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
		logger.info("Update erfolgreich");
	}
	
	private void benutzerHinzufuegen(List<String> neueBenutzer, String gruppe, String gruppeId) {
		for (String benutzer : neueBenutzer) {
			BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
			benutzerGruppeDB.setBenutzer(benutzer);
			benutzerGruppeDB.setGruppe(gruppe);
			benutzerGruppeDB.setGruppeId(gruppeId);
			repository.save(benutzerGruppeDB);
		}
	}
	
	private void benutzerLoeschen(List<String> benutzerliste, String gruppeId) {
		for (String benutzer : benutzerliste) {
			repository.deleteByBenutzerAndGruppeId(benutzer, gruppeId);
		}
	}
	
}
