package mops.termine2.services;

import mops.termine2.database.UmfrageAntwortRepository;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.models.Umfrage;
import mops.termine2.util.IntegerToolkit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmfrageErgebnisService {
	
	private UmfrageAntwortRepository antwortRepo;
	
	public UmfrageErgebnisService(UmfrageAntwortRepository antwortRepo) {
		this.antwortRepo = antwortRepo;
	}
	
	public String berechneErgebnisUmfrage(Umfrage umfrage) {
		List<UmfrageAntwortDB> umfrageAntwortDBS =
			antwortRepo.findAllByUmfrageLink(umfrage.getLink());
		
		List<String> vorschlaege = umfrage.getVorschlaege();
		
		int[] ja = new int[vorschlaege.size()];
		int[] nein = new int[vorschlaege.size()];
		
		for (UmfrageAntwortDB umfrageAntwortDB : umfrageAntwortDBS) {
			int index = vorschlaege.indexOf(umfrageAntwortDB.getUmfrage().getAuswahlmoeglichkeit());
			switch (umfrageAntwortDB.getAntwort()) {
			case JA:
				ja[index]++;
				break;
			case NEIN:
				nein[index]++;
				break;
			default:
				break;
			}
		}
		
		List<Integer> highest = IntegerToolkit.findHighestIndex(ja);
		
		return umfrage.getVorschlaege().get(highest.get(0));
	}
}
