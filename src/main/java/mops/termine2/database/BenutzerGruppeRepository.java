package mops.termine2.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.BenutzerGruppeDB;

@Repository
public interface BenutzerGruppeRepository extends CrudRepository<BenutzerGruppeDB, Long> {
	
	List<BenutzerGruppeDB> findByBenutzer(String benutzer);
	
	List<BenutzerGruppeDB> findByBenutzerAndGruppe(String benutzer, String gruppe);
	
	@Transactional
	void deleteAllByGruppeId(Long gruppeId);
	
	BenutzerGruppeDB findByBenutzerAndGruppeId(String benutzer, Long gruppeId);
	
	@Query("select db.benutzer from BenutzerGruppeDB db where db.gruppeId like :gruppeId")
	List<String> findBenutzerByGruppeId(@Param("gruppeId") Long gruppeId);
	
	@Transactional
	void deleteByBenutzerAndGruppeId(String benutzer, Long gruppeId);
	
	@Query("select distinct db.gruppe from BenutzerGruppeDB db where db.gruppeId like :gruppeId")
	Optional<String> findGruppeByGruppeId(@Param("gruppeId") Long gruppeId);
	
}
