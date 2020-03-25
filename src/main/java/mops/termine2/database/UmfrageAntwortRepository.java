package mops.termine2.database;

import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UmfrageAntwortRepository extends CrudRepository<UmfrageAntwortDB, Long> {
	
	@Query
	List<UmfrageAntwortDB> findByBenutzerAndUmfrageLink(String benutzer, String link);
	
	@Query
	List<UmfrageAntwortDB> findAllByUmfrageLink(String link);
	
	@Transactional
	@Query("delete from UmfrageAntwortDB db where db.umfrage.link like :link")
	void deleteAllByUmfrageLink(@Param("link") String link);
	
	@Transactional
	@Query("delete from UmfrageAntwortDB db where db.umfrage.loeschdatum < :timeNow")
	void deleteOutdated(@Param("timeNow") LocalDateTime timeNow);
	
	@Query("select db.umfrage from UmfrageAntwortDB db where db.benutzer like :benutzer")
	List<UmfrageDB> findUmfrageDbByBenutzer(@Param("benutzer") String benutzer);
	
}
