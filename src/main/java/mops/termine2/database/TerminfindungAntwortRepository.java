package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungAntwortDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminfindungAntwortRepository extends CrudRepository<TerminfindungAntwortDB, Long> {
	
	List<TerminfindungAntwortDB> findByBenutzerAndTerminfindungLink(String benutzer, String link);
	
	List<TerminfindungAntwortDB> findAllByTerminfindungLink(String link);
	
	@Query("delete from TerminfindungAntwortDB db where db.terminfindung.link like :link and db.benutzer like :benutzer")
	void deleteAllByTerminfindungLinkAndBenutzer(@Param("link") String link, @Param("benutzer") String benutzer);
	
	@Query("delete from TerminfindungAntwortDB db where db.terminfindung.link like  :link")
	void deleteAllByTerminfindungLink(@Param("link") String link);
}

