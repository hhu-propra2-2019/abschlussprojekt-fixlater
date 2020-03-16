package mops.termine2.services;

import lombok.AllArgsConstructor;
import mops.termine2.database.TerminfindungRepository;
import mops.termine2.database.UmfrageRepository;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LinkService {
	
	private transient TerminfindungRepository terminfindungRepo;
	
	private transient UmfrageRepository umfrageRepo;
	
	public String generiereEindeutigenLink() {
		String link = UUID.randomUUID().toString();
		
		while (!pruefeEindeutigkeitLink(link)) {
			link = UUID.randomUUID().toString();
		}
		
		return link;
	}
	
	public Boolean pruefeEindeutigkeitLink(String link) {
		List<TerminfindungDB> terminfindungDBs = terminfindungRepo.findByLink(link);
		List<UmfrageDB> umfrageDBs = umfrageRepo.findByLink(link);
		
		if (terminfindungDBs.isEmpty() && umfrageDBs.isEmpty()) {
			return true;
		}
		return false;
	}
	
	
}
