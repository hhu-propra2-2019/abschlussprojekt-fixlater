package mops.termine2.database;

import com.github.javafaker.Faker;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Modus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
public class DatabaseInitializer implements ServletContextInitializer {
	
	public static final int ANZAHL_GRUPPEN = 10;
	public static final int ANZAHL_BENUTZER = 10;
	public static final int ANZAHL_OPTIONEN = 10;
	
	@Autowired
	BenutzerGruppeRepository benutzerGruppeRepository;
	
	@Autowired
	KommentarRepository kommentarRepository;
	
	@Autowired
	TerminfindungAntwortRepository terminfindungAntwortRepository;
	
	@Autowired
	TerminfindungRepository terminfindungRepository;
	
	@Autowired
	UmfrageAntwortRepository umfrageAntwortRepository;
	
	@Autowired
	UmfrageRepository umfrageRepository;
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		System.out.println("Befülle Datenbank!");
		final Faker faker = new Faker(Locale.GERMAN);
		IntStream.range(0, ANZAHL_GRUPPEN).forEach(value1 -> {
		
			String gruppeName = faker.book().title();
			Long gruppeId = ThreadLocalRandom.current().nextLong(10000);
			
			IntStream.range(0, ANZAHL_BENUTZER).forEach(value2 -> {
				
				final BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
				benutzerGruppeDB.setBenutzer(faker.name().username());
				benutzerGruppeDB.setGruppe(gruppeName);
				benutzerGruppeDB.setGruppeId(gruppeId);
				benutzerGruppeDB.setId(ThreadLocalRandom.current().nextLong(10000));
				
				if(Math.random() < 0.5) {
					fakeTerminfindungGruppe(faker, benutzerGruppeDB);
				} else {
					fakeUmfrageGruppe(faker, benutzerGruppeDB);
				}
				
				this.benutzerGruppeRepository.save(benutzerGruppeDB);
			});
		});
	}
	
	public void fakeTerminfindungGruppe(Faker faker, BenutzerGruppeDB benutzerGruppeDB) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.funnyName().name();
		String ort = faker.address().cityName();
		String titel = faker.friends().quote();
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final TerminfindungDB terminfindungdb = new TerminfindungDB();
			terminfindungdb.setBeschreibung(beschreibung);
			terminfindungdb.setErsteller(benutzerGruppeDB.getBenutzer());
			//terminfindungdb.setFrist(faker.);
			terminfindungdb.setGruppe(benutzerGruppeDB.getGruppe());
			terminfindungdb.setLink(link);
			//terminfindungdb.setLoeschdatum();
			terminfindungdb.setOrt(ort);
			terminfindungdb.setModus(Modus.GRUPPE);
			//terminfindungdb.setTermin();
			terminfindungdb.setTitel(titel);
			
			this.terminfindungRepository.save(terminfindungdb);
		});
	}
	
	public void fakeUmfrageGruppe(Faker faker, BenutzerGruppeDB benutzerGruppeDB) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.funnyName().name();
		String titel = faker.friends().quote();
		Long maxAntwortAnzahl = ThreadLocalRandom.current().nextLong(1, 10);
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setBeschreibung(beschreibung);
			umfrageDB.setErsteller(benutzerGruppeDB.getBenutzer());
			//terminfindungdb.setFrist(faker.);
			umfrageDB.setGruppe(benutzerGruppeDB.getGruppe());
			umfrageDB.setLink(link);
			//terminfindungdb.setLoeschdatum();
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel(titel);
			umfrageDB.setAuswahlmoeglichkeit(faker.harryPotter().spell());
			umfrageDB.setMaxAntwortAnzahl(maxAntwortAnzahl);
			
			this.umfrageRepository.save(umfrageDB);
		});
	}
	
	
}
