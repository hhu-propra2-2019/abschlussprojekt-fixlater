package mops.termine2.database;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mops.termine2.database.entities.UmfrageAntwortDB;
import mops.termine2.database.entities.UmfrageDB;

@Repository
public interface UmfrageAntwortRepository extends CrudRepository<UmfrageAntwortDB, Long> {
	
	@Query
	List<UmfrageAntwortDB> findByBenutzerAndUmfrageLink(String benutzer, String link);
	
	@Query
	List<UmfrageAntwortDB> findAllByUmfrageLink(String link);
	
	@Query("delete from UmfrageAntwortDB db where db.umfrage.link like :link and db.benutzer like :benutzer")
	void deleteAllByUmfrageLinkAndBenutzer(@Param("link") String link, @Param("benutzer") String benutzer);
	
	@Query("delete from UmfrageAntwortDB db where db.umfrage.link like :link")
	void deleteAllByUmfrageLink(@Param("link") String link);
	
	@Query("delete from UmfrageAntwortDB db where db.umfrage.loeschdatum < :timeNow")
	void deleteOutdated(@Param("timeNow") LocalDateTime timeNow);
	
	@Query("select db.umfrage from UmfrageAntwortDB db where db.benutzer like :benutzer")
	List<UmfrageDB> findUmfrageDbByBenutzer(@Param("benutzer") String benutzer);
	
}
