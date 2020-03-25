package mops.termine2.database;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.TerminfindungAntwortDB;
import mops.termine2.database.entities.TerminfindungDB;

@Repository
public interface TerminfindungAntwortRepository extends CrudRepository<TerminfindungAntwortDB, Long> {
	
	List<TerminfindungAntwortDB> findByBenutzerAndTerminfindungLink(String benutzer, String link);
	
	List<TerminfindungAntwortDB> findAllByTerminfindungLink(String link);
	
	@Transactional
	void deleteAllByTerminfindungLinkAndBenutzer(@Param("link") String link, @Param("benutzer") String benutzer);
	
	@Transactional
	@Query("delete from TerminfindungAntwortDB db where db.terminfindung.link like  :link")
	void deleteByLink(@Param("link") String link);
	
	@Transactional
	@Query("delete from TerminfindungAntwortDB db where db.terminfindung.loeschdatum < :timeNow")
	void loescheAelterAls(@Param("timeNow") LocalDateTime timeNow);
	
	@Query("select db.terminfindung from TerminfindungAntwortDB db where db.benutzer like :benutzer")
	List<TerminfindungDB> findTerminfindungDbByBenutzer(@Param("benutzer") String benutzer);
	
	@Transactional
	void deleteByTerminfindungLoeschdatumBefore(LocalDateTime timeNow);
}
