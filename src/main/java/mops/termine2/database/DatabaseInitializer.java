package mops.termine2.database;

import com.github.javafaker.Faker;
import mops.termine2.database.entities.BenutzerGruppeDB;
import mops.termine2.database.entities.KommentarDB;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DatabaseInitializer implements ServletContextInitializer {
	
	private static final int ANZAHL_GRUPPEN = 5;
	
	private static final int ANZAHL_BENUTZER_GRUPPE = 5;
	
	private static final int ANZAHL_STUDENTEN = 10;
	
	private static final int ANZAHL_OPTIONEN = 4;
	
	private static final double ENTSCHEIDUNGSWERT1 = 0.5;
	
	private static final double ENTSCHEIDUNGSWERT2 = 0.33;
	
	private static final int ANZAHL_LINK = 5;
	
	private static final int MAX_ANZAHL_KOMMENTARE = 3;
	
	private static final boolean EINGESCHALTET = false;
	
	
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
			for (int value1 = 0; value1 < ANZAHL_GRUPPEN; value1++) {
				String gruppeName = faker.book().title();
				Long gruppeId = ThreadLocalRandom.current().nextLong(10000);
				List<Integer> studenten = IntStream.rangeClosed(1, ANZAHL_STUDENTEN)
					.boxed().collect(Collectors.toList());
				
				for (int value2 = 0; value2 < ANZAHL_BENUTZER_GRUPPE; value2++) {
					final BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
					int indexStudent = new Random().nextInt(ANZAHL_STUDENTEN - value2);
					benutzerGruppeDB.setBenutzer("studentin" + studenten.get(indexStudent));
					studenten.remove(indexStudent);
					benutzerGruppeDB.setGruppe(gruppeName);
					benutzerGruppeDB.setGruppeId(gruppeId);
					benutzerGruppeDB.setId(ThreadLocalRandom.current().nextLong(10000));
					
					if (Math.random() < ENTSCHEIDUNGSWERT1) {
						fakeTerminfindungGruppe(faker, benutzerGruppeDB, value1, Math.random());
					} else {
						fakeUmfrageGruppe(faker, benutzerGruppeDB, value1, Math.random());
					}
					
					this.benutzerGruppeRepository.save(benutzerGruppeDB);
				}
			}
			
			IntStream.range(1, 3).forEach(gruppeNummer -> {
				long gruppeId = ThreadLocalRandom.current().nextLong(10000);
				
				IntStream.range(1, ANZAHL_BENUTZER_GRUPPE - 1).forEach(studentNummer -> {
					final BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
					benutzerGruppeDB.setBenutzer("studentin" + studentNummer);
					benutzerGruppeDB.setGruppe("FIXLATER" + gruppeNummer);
					benutzerGruppeDB.setGruppeId(gruppeId);
					benutzerGruppeDB.setId(ThreadLocalRandom.current().nextLong(10000));
					fakeTerminfindungGruppe(faker, benutzerGruppeDB, studentNummer, 0);
					fakeUmfrageGruppe(faker, benutzerGruppeDB, studentNummer, 0);
					fakeTerminfindungGruppe(faker, benutzerGruppeDB, studentNummer, 1);
					fakeUmfrageGruppe(faker, benutzerGruppeDB, studentNummer, 1);
					
					this.benutzerGruppeRepository.save(benutzerGruppeDB);
				});
			});
			
			int benutzerZaehler = 1;
			for (int value2 = 0; value2 < ANZAHL_LINK; value2++) {
				
				if (Math.random() < ENTSCHEIDUNGSWERT1) {
					fakeTerminfindungLink(faker, "studentin" + benutzerZaehler);
					fakeTerminfindungLink(faker, "orga" + benutzerZaehler);
				} else {
					fakeUmfrageLink(faker, "studentin" + benutzerZaehler);
					fakeUmfrageLink(faker, "orga" + benutzerZaehler);
				}
				benutzerZaehler++;
			}
		}
	}
	
	public void fakeTerminfindungGruppe(Faker faker, BenutzerGruppeDB benutzerGruppeDB, int gruppeZaehler,
										double entscheidungswert) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.name().firstName() + benutzerGruppeDB.getId();
		String ort = faker.address().cityName();
		String titel = faker.friends().quote();
		LocalDateTime frist = setzeDatumZukunftOderVergangenheit(entscheidungswert);
		LocalDateTime loeschdatum = frist.plusDays(90);
		LocalDateTime ergebnis = frist.plusDays(90);
		int antwortGrenze = new Random().nextInt(4);
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final TerminfindungDB terminfindungdb = new TerminfindungDB();
			terminfindungdb.setBeschreibung(beschreibung);
			terminfindungdb.setErsteller(benutzerGruppeDB.getBenutzer());
			terminfindungdb.setFrist(frist);
			terminfindungdb.setGruppeId(benutzerGruppeDB.getGruppeId());
			terminfindungdb.setLink(link);
			terminfindungdb.setLoeschdatum(loeschdatum);
			terminfindungdb.setOrt(ort);
			terminfindungdb.setModus(Modus.GRUPPE);
			terminfindungdb.setTermin(frist.plusDays(new Random().nextInt(80)));
			terminfindungdb.setTitel(titel);
			terminfindungdb.setErgebnis(ergebnis);
			Random zufallszahl = new Random();
			terminfindungdb.setEinmaligeAbstimmung(zufallszahl.nextBoolean());
			
			this.terminfindungRepository.save(terminfindungdb);
			
			fakeTerminfindungAntwortenGruppe(gruppeZaehler, terminfindungdb, faker, antwortGrenze);
		});
		fakeKommentare(faker, link, loeschdatum, frist);
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
	
	public void fakeTerminfindungLink(Faker faker, String benutzer) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.funnyName().name();
		String ort = faker.address().cityName();
		String titel = faker.friends().quote();
		LocalDateTime frist = LocalDateTime.now().plusDays(new Random().nextInt(90))
			.minusDays(new Random().nextInt(90));
		LocalDateTime ergebnis = LocalDateTime.now().plusDays(new Random().nextInt(90))
			.minusDays(new Random().nextInt(90));
		LocalDateTime loeschdatum = frist.plusDays(90);
		int antwortGrenze = new Random().nextInt(3);
		
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
			terminfindungdb.setErgebnis(ergebnis);
			Random zufallszahl = new Random();
			terminfindungdb.setEinmaligeAbstimmung(zufallszahl.nextBoolean());
			
			this.terminfindungRepository.save(terminfindungdb);
			
			fakeTerminfindungAntwortenLink(terminfindungdb, faker, antwortGrenze);
		});
		fakeKommentare(faker, link, loeschdatum, frist);
	}
	
	public void fakeTerminfindungAntwortenLink(TerminfindungDB terminfindungDB, Faker faker, int grenze) {
		IntStream.range(grenze + 1, 10).forEach(value -> {
			final TerminfindungAntwortDB terminfindungAntwortDB = new TerminfindungAntwortDB();
			double benutzerWahl = Math.random();
			if (benutzerWahl < ENTSCHEIDUNGSWERT1) {
				terminfindungAntwortDB.setBenutzer("studentin" + value);
			} else {
				terminfindungAntwortDB.setBenutzer("orga" + value);
			}
			terminfindungAntwortDB.setTerminfindung(terminfindungDB);
			
			double option = Math.random();
			if (option < ENTSCHEIDUNGSWERT1) {
				terminfindungAntwortDB.setAntwort(Antwort.JA);
			} else {
				terminfindungAntwortDB.setAntwort(Antwort.NEIN);
			}
			
			double pseudonymWahl = Math.random();
			if (pseudonymWahl < ENTSCHEIDUNGSWERT1) {
				terminfindungAntwortDB.setPseudonym(faker.harryPotter().character() + value);
			} else {
				if (benutzerWahl < ENTSCHEIDUNGSWERT1) {
					terminfindungAntwortDB.setPseudonym("studentin" + value);
				} else {
					terminfindungAntwortDB.setPseudonym("orga" + value);
				}
			}
			this.terminfindungAntwortRepository.save(terminfindungAntwortDB);
		});
	}
	
	public void fakeUmfrageGruppe(Faker faker, BenutzerGruppeDB benutzerGruppeDB, int gruppeZaehler,
								  double entscheidungswert) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.name().firstName() + benutzerGruppeDB.getId();
		String titel = faker.friends().quote();
		Long maxAntwortAnzahl = ThreadLocalRandom.current().nextLong(1, ANZAHL_OPTIONEN);
		LocalDateTime frist = setzeDatumZukunftOderVergangenheit(entscheidungswert);
		LocalDateTime loeschdatum = frist.plusDays(90);
		int antwortGrenze = new Random().nextInt(4);
		
		IntStream.range(0, ANZAHL_OPTIONEN).forEach(value -> {
			final UmfrageDB umfrageDB = new UmfrageDB();
			umfrageDB.setBeschreibung(beschreibung);
			umfrageDB.setErsteller(benutzerGruppeDB.getBenutzer());
			umfrageDB.setFrist(frist);
			umfrageDB.setGruppeId(benutzerGruppeDB.getGruppeId());
			umfrageDB.setLink(link);
			umfrageDB.setLoeschdatum(loeschdatum);
			umfrageDB.setModus(Modus.GRUPPE);
			umfrageDB.setTitel(titel);
			umfrageDB.setAuswahlmoeglichkeit(faker.harryPotter().spell());
			umfrageDB.setMaxAntwortAnzahl(maxAntwortAnzahl);
			
			this.umfrageRepository.save(umfrageDB);
			
			fakeUmfrageAntwortenGruppe(gruppeZaehler, umfrageDB, faker, antwortGrenze);
		});
		fakeKommentare(faker, link, loeschdatum, frist);
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
	
	public void fakeUmfrageLink(Faker faker, String benutzer) {
		
		String beschreibung = faker.lorem().sentence();
		String link = faker.funnyName().name();
		String titel = faker.friends().quote();
		Long maxAntwortAnzahl = ThreadLocalRandom.current().nextLong(1, ANZAHL_OPTIONEN);
		LocalDateTime frist = LocalDateTime.now().plusDays(new Random().nextInt(90))
			.minusDays(new Random().nextInt(90));
		LocalDateTime loeschdatum = frist.plusDays(90);
		int antwortGrenze = new Random().nextInt(3);
		
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
			
			fakeUmfrageAntwortenLink(umfrageDB, faker, antwortGrenze);
		});
		fakeKommentare(faker, link, loeschdatum, frist);
	}
	
	public void fakeUmfrageAntwortenLink(UmfrageDB umfrageDB, Faker faker, int grenze) {
		IntStream.range(grenze + 1, 10).forEach(value -> {
			final UmfrageAntwortDB umfrageAntwortDB = new UmfrageAntwortDB();
			double benutzerWahl = Math.random();
			if (benutzerWahl < ENTSCHEIDUNGSWERT1) {
				umfrageAntwortDB.setBenutzer("studentin" + value);
			} else {
				umfrageAntwortDB.setBenutzer("orga" + value);
			}
			umfrageAntwortDB.setUmfrage(umfrageDB);
			
			double option = Math.random();
			if (option < ENTSCHEIDUNGSWERT1) {
				umfrageAntwortDB.setAntwort(Antwort.JA);
			} else {
				umfrageAntwortDB.setAntwort(Antwort.NEIN);
			}
			
			double pseudonymWahl = Math.random();
			if (pseudonymWahl < ENTSCHEIDUNGSWERT1) {
				umfrageAntwortDB.setPseudonym(faker.harryPotter().character() + value);
			} else {
				if (benutzerWahl < ENTSCHEIDUNGSWERT1) {
					umfrageAntwortDB.setPseudonym("studentin" + value);
				} else {
					umfrageAntwortDB.setPseudonym("orga" + value);
				}
			}
			this.umfrageAntwortRepository.save(umfrageAntwortDB);
		});
	}
	
	public void fakeKommentare(Faker faker, String link, LocalDateTime loeschdatum, LocalDateTime frist) {
		int kommentarAnzahl = new Random().nextInt(MAX_ANZAHL_KOMMENTARE + 1);
		IntStream.range(0, kommentarAnzahl).forEach(value -> {
			final KommentarDB kommentarDB = new KommentarDB();
			int tage = new Random().nextInt(120);
			kommentarDB.setErstellungsdatum(loeschdatum.minusDays(tage));
			kommentarDB.setPseudonym(faker.friends().character());
			kommentarDB.setLink(link);
			kommentarDB.setInhalt(faker.friends().quote());
			
			this.kommentarRepository.save(kommentarDB);
		});
	}
	
	private LocalDateTime setzeDatumZukunftOderVergangenheit(double entscheidungswert) {
		if (entscheidungswert < ENTSCHEIDUNGSWERT1) {
			return LocalDateTime.now().minusDays(new Random().nextInt(30));
		}
		return LocalDateTime.now().plusDays(new Random().nextInt(60));
	}
}
