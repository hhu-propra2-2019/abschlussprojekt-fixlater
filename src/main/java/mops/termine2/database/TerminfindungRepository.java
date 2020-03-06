package mops.termine2.database;

import mops.termine2.database.entities.Terminfindung;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminfindungRepository extends CrudRepository<Terminfindung, Long> {
	
}
