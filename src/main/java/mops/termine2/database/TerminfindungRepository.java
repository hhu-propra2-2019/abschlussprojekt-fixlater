package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TerminfindungRepository extends CrudRepository<TerminfindungDB, Long> {
	
	List<TerminfindungDB> findByLink(String link);
	
	List<TerminfindungDB> findByErstellerOrderByFristAsc(String ersteller);
	
	List<TerminfindungDB> findByGruppeIdOrderByFristAsc(Long gruppeId);
	
	@Transactional
	void deleteByLink(String link);
	
	TerminfindungDB findByLinkAndTermin(String link, LocalDateTime termin);
	
	@Transactional
	void deleteByLoeschdatumBefore(LocalDateTime timeNow);
	
	List<TerminfindungDB> findByFristBeforeAndErgebnisIsNull(LocalDateTime timeNow);
	
	List<TerminfindungDB> findByLoeschdatumBefore(LocalDateTime timeNow);
}
