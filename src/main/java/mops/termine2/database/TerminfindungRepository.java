package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminfindungRepository extends CrudRepository<TerminfindungDB, Long> {
	
	List<TerminfindungDB> findByLink(String link);
	
	
	List<TerminfindungDB> findByErsteller(String ersteller);
	
	List<TerminfindungDB> findByGruppe(String gruppe);
	
}
