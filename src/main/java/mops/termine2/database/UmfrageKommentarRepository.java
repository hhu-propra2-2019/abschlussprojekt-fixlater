package mops.termine2.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UmfrageKommentarRepository extends CrudRepository<UmfrageKommentar, Long> {
	
}
