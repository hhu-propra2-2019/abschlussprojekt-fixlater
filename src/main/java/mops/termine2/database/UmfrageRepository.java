package mops.termine2.database;

import mops.termine2.database.entities.UmfrageDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
}
