package mops.termine2.database;

import mops.termine2.database.entities.Gruppe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GruppeRepository extends CrudRepository<Gruppe, Long> {
	
}
