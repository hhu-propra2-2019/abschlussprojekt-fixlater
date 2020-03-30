package mops.termine2.database;

import mops.termine2.database.entities.BenutzerGruppeDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenutzerGruppeRepository extends CrudRepository<BenutzerGruppeDB, Long> {
	
	List<BenutzerGruppeDB> findByBenutzer(String benutzer);
	
	List<BenutzerGruppeDB> findByGruppeId(String gruppeId);
	
	BenutzerGruppeDB findByBenutzerAndGruppeId(String benutzer, String gruppeId);
	
	@Transactional
	void deleteAllByGruppeId(String gruppeId);
	
	@Query("select db.benutzer from BenutzerGruppeDB db where db.gruppeId like :gruppeId")
	List<String> findBenutzerByGruppeId(@Param("gruppeId") String gruppeId);
	
	@Transactional
	void deleteByBenutzerAndGruppeId(String benutzer, String gruppeId);
	
	@Query("select distinct db.gruppe from BenutzerGruppeDB db where db.gruppeId like :gruppeId")
	Optional<String> findGruppeByGruppeId(@Param("gruppeId") String gruppeId);
	
}
