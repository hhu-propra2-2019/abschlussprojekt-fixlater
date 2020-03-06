package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungKommentar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminfindungKommentarRepository extends CrudRepository<TerminfindungKommentar, Long> {
	
}
