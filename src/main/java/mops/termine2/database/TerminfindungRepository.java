package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TerminfindungRepository extends CrudRepository<TerminfindungDB, Long> {
	
	List<TerminfindungDB> findByLink(String link);
	
	@Query("select db from TerminfindungDB db where db.ersteller like :ersteller order by db.frist")
	List<TerminfindungDB> findByErsteller(@Param("ersteller") String ersteller);
	
	@Query("select db from TerminfindungDB db where db.gruppeId = :gruppeId order by db.frist")
	List<TerminfindungDB> findByGruppeId(@Param("gruppeId") Long gruppeId);
	
	@Transactional
	@Query("delete from TerminfindungDB db where db.link like : link")
	void deleteByLink(@Param("link") String link);
	
	@Transactional
	@Query("delete from TerminfindungDB db where db.loeschdatum < :timeNow")
	void loescheAelterAls(@Param("timeNow") LocalDateTime timeNow);
	
	TerminfindungDB findByLinkAndTermin(String link, LocalDateTime termin);
}
