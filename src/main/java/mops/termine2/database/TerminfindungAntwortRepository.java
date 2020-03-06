package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungAntwort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminfindungAntwortRepository extends CrudRepository<TerminfindungAntwort, Long> {
	
}
