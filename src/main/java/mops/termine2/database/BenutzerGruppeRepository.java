package mops.termine2.database;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.BenutzerGruppeDB;

@Repository
public interface BenutzerGruppeRepository extends CrudRepository<BenutzerGruppeDB, Long> {
	
	List<BenutzerGruppeDB> findByBenutzer(String benutzer);
	
	@Transactional
	void deleteAllByGruppeId(Long id);
}
