package mops.termine2.database;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mops.termine2.database.entities.UmfrageAntwortDB;

@Repository
public interface UmfrageAntwortRepository extends CrudRepository<UmfrageAntwortDB, Long> {
	
	@Query
	List<UmfrageAntwortDB> findByBenutzerAndUmfrageLink(String benutzer, String link);
	
	@Query("delete from UmfrageAntwortDB db where db.umfrage.link like :link and db.benutzer like :benutzer")
	void deleteAllByUmfrageLinkAndBenutzer(@Param("link") String link, @Param("benutzer") String benutzer);
	
}
