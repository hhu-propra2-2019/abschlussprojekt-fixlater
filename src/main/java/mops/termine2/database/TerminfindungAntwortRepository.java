package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungAntwortDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminfindungAntwortRepository extends CrudRepository<TerminfindungAntwortDB, Long> {
	
	List<TerminfindungAntwortDB> findByBenutzerAndTerminfindungLink(String benutzer, String link);
	
	List<TerminfindungAntwortDB> findAllByTerminfindungLink(String link);
	
	void deleteAllByTerminfindungLinkAAndBenutzer(String link, String benutzer);
}

