package mops.termine2.database;

import mops.termine2.database.entities.KommentarDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KommentarRepository extends CrudRepository<KommentarDB, Long> {
	
}
