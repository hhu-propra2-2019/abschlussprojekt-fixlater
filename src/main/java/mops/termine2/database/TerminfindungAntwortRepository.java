package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungAntwortDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminfindungAntwortRepository extends CrudRepository<TerminfindungAntwortDB, Long> {
	
}
