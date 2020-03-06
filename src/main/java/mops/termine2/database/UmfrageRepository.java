package mops.termine2.database;

import mops.termine2.database.entities.Umfrage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UmfrageRepository extends CrudRepository<Umfrage, Long> {
	
}
