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
	
	List<BenutzerGruppeDB> findByGruppeId(Long gruppeId);
	
	BenutzerGruppeDB findByBenutzerAndGruppeId(String benutzer, Long gruppeId);
	
	@Transactional
	void deleteAllByGruppeId(Long gruppeId);
	
	@Query("select db.benutzer from BenutzerGruppeDB db where db.gruppeId = :gruppeId")
	List<String> findBenutzerByGruppeId(@Param("gruppeId") Long gruppeId);
	
	@Transactional
	void deleteByBenutzerAndGruppeId(String benutzer, Long gruppeId);
	
	@Query("select distinct db.gruppe from BenutzerGruppeDB db where db.gruppeId = :gruppeId")
	Optional<String> findGruppeByGruppeId(@Param("gruppeId") Long gruppeId);
	
}
