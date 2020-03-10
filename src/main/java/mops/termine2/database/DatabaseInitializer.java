package mops.termine2.database;

import com.github.javafaker.Faker;
import mops.termine2.database.entities.BenutzerGruppeDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class DatabaseInitializer implements ServletContextInitializer {
	
	public static final int N = 10;
	
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
		IntStream.range(0, N).forEach(value1 -> {
		
			String gruppeName = faker.book().title();
			Long gruppeId = new Random().nextLong();
			
			IntStream.range(0, N).forEach(value2 -> {
				
				final BenutzerGruppeDB benutzerGruppeDB = new BenutzerGruppeDB();
				benutzerGruppeDB.setBenutzer(faker.name().username());
				benutzerGruppeDB.setGruppe(gruppeName);
				benutzerGruppeDB.setGruppeId(gruppeId);
				benutzerGruppeDB.setId(new Random().nextLong());
				
				this.benutzerGruppeRepository.save(benutzerGruppeDB);
			
			});
		});
	}
	
	
	
}
