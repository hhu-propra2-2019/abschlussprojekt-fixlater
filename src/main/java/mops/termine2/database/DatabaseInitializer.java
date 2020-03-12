package mops.termine2.database;

import com.github.javafaker.Faker;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;
import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import mops.termine2.enums.Antwort;
import mops.termine2.enums.Modus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
public class DatabaseInitializer implements ServletContextInitializer {
	
	private static final int ANZAHL_GRUPPEN = 10;
	
	private static final int ANZAHL_BENUTZER_GRUPPE = 10;
	
	private static final int ANZAHL_OPTIONEN = 4;
	
	private static final double ENTSCHEIDUNGSWERT1 = 0.5;
	
	private static final double ENTSCHEIDUNGSWERT2 = 0.33;
	
	private static final int ANZAHL_LINK = 20;
	
	private static final boolean EINGESCHALTET = true;
	
	
	@Autowired
	private transient BenutzerGruppeRepository benutzerGruppeRepository;
	
	@Autowired
	private transient KommentarRepository kommentarRepository;
	
	@Autowired
	private transient TerminfindungAntwortRepository terminfindungAntwortRepository;
	
	@Autowired
	private transient TerminfindungRepository terminfindungRepository;
	
	@Autowired
	private transient UmfrageAntwortRepository umfrageAntwortRepository;
	
	@Autowired
	private transient UmfrageRepository umfrageRepository;
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		if (EINGESCHALTET) {
			System.out.println("Befülle Datenbank!");
			final Faker faker = new Faker(Locale.GERMAN);
			int studentenZaehler = 1;
			for (int value1 = 0; value1 < ANZAHL_GRUPPEN; value1++) {
				// TODO Rollen: orga, orga1, orga2, orga3, actuator
				String gruppeName = faker.book().title();
				Long gruppeId = ThreadLocalRandom.current().nextLong(10000);
				
				for (int value2 = 0; value2 < ANZAHL_BENUTZER_GRUPPE; value2++) {
					final BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
					benutzerGruppeDB.setBenutzer("studentin" + studentenZaehler);
					studentenZaehler++;
					benutzerGruppeDB.setGruppe(gruppeName);
					benutzerGruppeDB.setGruppeId(gruppeId);
					benutzerGruppeDB.setId(ThreadLocalRandom.current().nextLong(10000));
					
					if (Math.random() < ENTSCHEIDUNGSWERT1) {
						fakeTerminfindungGruppe(faker, benutzerGruppeDB, value1);
					} else {
						fakeUmfrageGruppe(faker, benutzerGruppeDB, value1);
					}
					
					this.benutzerGruppeRepository.save(benutzerGruppeDB);
				}
			}
			
			studentenZaehler = 1;
			for (int value2 = 0; value2 < ANZAHL_LINK; value2++) {
				
				if (Math.random() < ENTSCHEIDUNGSWERT1) {
					fakeTerminfindungLink(faker, "studentin" + studentenZaehler);
					fakeTerminfindungLink(faker, "orga" + studentenZaehler);
				} else {
					fakeUmfrageLink(faker, "studentin" + studentenZaehler);
					fakeUmfrageLink(faker, "orga" + studentenZaehler);
				}
				studentenZaehler++;
			}
		}
	}
	
	public void fakeTerminfindungGruppe(Faker faker, BenutzerGruppeDB benutzerGruppeDB, int gruppeZaehler) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.name().firstName() + benutzerGruppeDB.getId();
		String ort = faker.address().cityName();
		String titel = faker.friends().quote();
		LocalDateTime frist = LocalDateTime.now().plusDays(new Random().nextInt(30))
				.plusMonths(new Random().nextInt(4));
		LocalDateTime loeschdatum = frist.plusDays(90);
		int antwortGrenze = new Random().nextInt(4);
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final TerminfindungDB terminfindungdb = new TerminfindungDB();
			terminfindungdb.setBeschreibung(beschreibung);
			terminfindungdb.setErsteller(benutzerGruppeDB.getBenutzer());
			terminfindungdb.setFrist(frist);
			terminfindungdb.setGruppe(benutzerGruppeDB.getGruppe());
			terminfindungdb.setLink(link);
			terminfindungdb.setLoeschdatum(loeschdatum);
			terminfindungdb.setOrt(ort);
			terminfindungdb.setModus(Modus.GRUPPE);
			terminfindungdb.setTermin(frist.plusDays(new Random().nextInt(80)));
			terminfindungdb.setTitel(titel);
			
			this.terminfindungRepository.save(terminfindungdb);
			
			fakeTerminfindungAntwortenGruppe(gruppeZaehler, terminfindungdb, faker, antwortGrenze);
		});
	}
	
	public void fakeTerminfindungAntwortenGruppe(int zaehler, TerminfindungDB terminfindungDB, Faker faker,
												 int grenze) {
		IntStream.range(zaehler * 10 + 1 + grenze, zaehler * 10 + 1 + ANZAHL_BENUTZER_GRUPPE).forEach(value -> {
			final TerminfindungAntwortDB terminfindungAntwortDB = new TerminfindungAntwortDB();
			terminfindungAntwortDB.setBenutzer("studentin" + value);
			terminfindungAntwortDB.setTerminfindung(terminfindungDB);
			double option = Math.random();
			if (option < ENTSCHEIDUNGSWERT2) {
				terminfindungAntwortDB.setAntwort(Antwort.JA);
			} else if (option < 2 * ENTSCHEIDUNGSWERT2) {
				terminfindungAntwortDB.setAntwort(Antwort.VIELLEICHT);
			} else {
				terminfindungAntwortDB.setAntwort(Antwort.NEIN);
			}
			
			if (option < ENTSCHEIDUNGSWERT1) {
				terminfindungAntwortDB.setPseudonym(faker.harryPotter().character() + value);
			} else {
				terminfindungAntwortDB.setPseudonym("studentin" + value);
			}
			this.terminfindungAntwortRepository.save(terminfindungAntwortDB);
		});
		
	}
	
	public void fakeUmfrageGruppe(Faker faker, BenutzerGruppeDB benutzerGruppeDB, int gruppeZaehler) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.name().firstName() + benutzerGruppeDB.getId();
		String titel = faker.friends().quote();
		Long maxAntwortAnzahl = ThreadLocalRandom.current().nextLong(1, ANZAHL_OPTIONEN);
		LocalDateTime frist = LocalDateTime.now().plusDays(new Random().nextInt(30))
				.plusMonths(new Random().nextInt(4));
		LocalDateTime loeschdatum = frist.plusDays(90);
		int antwortGrenze = new Random().nextInt(4);
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setBeschreibung(beschreibung);
			umfrageDB.setErsteller(benutzerGruppeDB.getBenutzer());
			umfrageDB.setFrist(frist);
			umfrageDB.setGruppe(benutzerGruppeDB.getGruppe());
			umfrageDB.setLink(link);
			umfrageDB.setLoeschdatum(loeschdatum);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel(titel);
			umfrageDB.setAuswahlmoeglichkeit(faker.harryPotter().spell());
			umfrageDB.setMaxAntwortAnzahl(maxAntwortAnzahl);
			
			this.umfrageRepository.save(umfrageDB);
			
			fakeUmfrageAntwortenGruppe(gruppeZaehler, umfrageDB, faker, antwortGrenze);
		});
	}
	
	public void fakeUmfrageAntwortenGruppe(int zaehler, UmfrageDB umfrageDB, Faker faker, int grenze) {
		IntStream.range(zaehler * 10 + 1 + grenze, zaehler * 10 + 1 + ANZAHL_BENUTZER_GRUPPE).forEach(value -> {
			final UmfrageAntwortDB umfrageAntwortDB = new UmfrageAntwortDB();
			umfrageAntwortDB.setBenutzer("studentin" + value);
			umfrageAntwortDB.setUmfrage(umfrageDB);
			double option = Math.random();
			if (option < ENTSCHEIDUNGSWERT1) {
				umfrageAntwortDB.setAntwort(Antwort.JA);
			} else {
				umfrageAntwortDB.setAntwort(Antwort.NEIN);
			}
			
			if (option < ENTSCHEIDUNGSWERT1) {
				umfrageAntwortDB.setPseudonym(faker.harryPotter().character() + value);
			} else {
				umfrageAntwortDB.setPseudonym("studentin" + value);
			}
			this.umfrageAntwortRepository.save(umfrageAntwortDB);
		});
		
	}
	
	public void fakeTerminfindungLink(Faker faker, String benutzer) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.funnyName().name();
		String ort = faker.address().cityName();
		String titel = faker.friends().quote();
		LocalDateTime frist = LocalDateTime.now().plusDays(new Random().nextInt(30))
				.plusMonths(new Random().nextInt(4));
		LocalDateTime loeschdatum = frist.plusDays(90);
		
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final TerminfindungDB terminfindungdb = new TerminfindungDB();
			terminfindungdb.setBeschreibung(beschreibung);
			terminfindungdb.setErsteller(benutzer);
			terminfindungdb.setFrist(frist);
			terminfindungdb.setLink(link);
			terminfindungdb.setLoeschdatum(loeschdatum);
			terminfindungdb.setOrt(ort);
			terminfindungdb.setModus(Modus.LINK);
			terminfindungdb.setTermin(frist.plusDays(new Random().nextInt(80)));
			terminfindungdb.setTitel(titel);
			
			this.terminfindungRepository.save(terminfindungdb);
		});
	}
	
	
	public void fakeUmfrageLink(Faker faker, String benutzer) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.funnyName().name();
		String titel = faker.friends().quote();
		Long maxAntwortAnzahl = ThreadLocalRandom.current().nextLong(1, ANZAHL_OPTIONEN);
		LocalDateTime frist = LocalDateTime.now().plusDays(new Random().nextInt(30))
				.plusMonths(new Random().nextInt(4));
		LocalDateTime loeschdatum = frist.plusDays(90);
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setBeschreibung(beschreibung);
			umfrageDB.setErsteller(benutzer);
			umfrageDB.setFrist(frist);
			umfrageDB.setLink(link);
			umfrageDB.setLoeschdatum(loeschdatum);
			umfrageDB.setModus(Modus.LINK);
			umfrageDB.setTitel(titel);
			umfrageDB.setAuswahlmoeglichkeit(faker.harryPotter().spell());
			umfrageDB.setMaxAntwortAnzahl(maxAntwortAnzahl);
			
			this.umfrageRepository.save(umfrageDB);
		});
	}
	
	
}
