package mops.termine2.database;

import mops.termine2.database.entities.UmfrageAntwort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UmfrageAntwortRepository extends CrudRepository<UmfrageAntwort, Long> {
	
}
