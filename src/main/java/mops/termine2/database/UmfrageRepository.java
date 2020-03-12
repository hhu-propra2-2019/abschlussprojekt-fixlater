package mops.termine2.database;


import mops.termine2.database.entities.UmfrageDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
	@Query
	List<UmfrageDB> findByLink(String link);
	
}
