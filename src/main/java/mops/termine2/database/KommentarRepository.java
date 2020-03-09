package mops.termine2.database;

import mops.termine2.database.entities.Kommentar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KommentarRepository extends CrudRepository<Kommentar, Long> {
	
}
