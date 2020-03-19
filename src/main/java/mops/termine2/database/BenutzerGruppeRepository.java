package mops.termine2.database;

import mops.termine2.database.entities.BenutzerGruppeDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenutzerGruppeRepository extends CrudRepository<BenutzerGruppeDB, Long> {
	
	List<BenutzerGruppeDB> findByBenutzer(String benutzer);
	
	List<BenutzerGruppeDB> findByBenutzerAndGruppe(String benutzer, String gruppe);
}
