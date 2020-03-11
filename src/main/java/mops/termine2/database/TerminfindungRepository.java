package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminfindungRepository extends CrudRepository<TerminfindungDB, Long> {
	
	@Query
	List<TerminfindungDB> findByLink(String link);
	
	@Query
	List<String> findLinkByErsteller(String ersteller);
	
	@Query
	List<String> findLinkByGruppe(String gruppe);
	
	/*
	@Query("select db.link from TerminfindungDB db where db.ersteller like :ersteller")
	List<String> findLinkByErsteller(@Param("ersteller") String e);
	
	 */
}
