package mops.termine2.database;

import mops.termine2.database.entities.GruppeDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GruppeRepository extends CrudRepository<GruppeDB, Long> {
	
}
