package mops.termine2.database;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mops.termine2.database.entities.UmfrageDB;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
	List<UmfrageDB> findByLink(String link);
	
	@Query("SELECT db FROM UmfrageDB db WHERE db.ersteller LIKE :ersteller ORDER BY db.frist")
	List<UmfrageDB> findByErsteller(@Param("ersteller") String ersteller);
	
	@Query("SELECT db FROM UmfrageDB db WHERE db.gruppe LIKE :gruppe ORDER BY db.frist")
	List<UmfrageDB> findByGruppe(@Param("gruppe") String gruppe);
	
	void deleteByLink(String link);
	
	void deleteByGruppe(String gruppe);
	
}
