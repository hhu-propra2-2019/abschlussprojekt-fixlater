package mops.termine2.database;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;

@Repository
public interface UmfrageAntwortRepository extends CrudRepository<UmfrageAntwortDB, Long> {
	
	@Query
	List<UmfrageAntwortDB> findByBenutzerAndUmfrageLink(String benutzer, String link);
	
	@Query
	List<UmfrageAntwortDB> findAllByUmfrageLink(String link);
	
	@Transactional
	void deleteAllByUmfrageLinkAndBenutzer(String link, String benutzer);
	
	@Transactional
	void deleteAllByUmfrageLink(@Param("link") String link);
	
	@Query("select db.umfrage from UmfrageAntwortDB db where db.benutzer like :benutzer")
	List<UmfrageDB> findUmfrageDbByBenutzer(@Param("benutzer") String benutzer);
	
	@Transactional
	void deleteByUmfrageLoeschdatumBefore(LocalDateTime timeNow);
	
}
