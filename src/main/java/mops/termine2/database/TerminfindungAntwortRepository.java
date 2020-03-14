package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungAntwortDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminfindungAntwortRepository extends CrudRepository<TerminfindungAntwortDB, Long> {
	
	@Query("delete from TerminfindungAntwortDB db where db.terminfindung.link like :link")
	void deleteByLink(@Param("link") String link);
	
}
