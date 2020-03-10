package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminfindungRepository extends CrudRepository<TerminfindungDB, Long> {
	
}
