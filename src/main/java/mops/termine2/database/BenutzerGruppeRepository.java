package mops.termine2.database;

import mops.termine2.database.entities.BenutzerGruppeDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenutzerGruppeRepository extends CrudRepository<BenutzerGruppeDB, Long> {
	
}
