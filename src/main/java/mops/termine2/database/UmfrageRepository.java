package mops.termine2.database;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mops.termine2.database.entities.UmfrageDB;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
	List<UmfrageDB> findByLink(String link);
	
	@Query("select db from UmfrageDB db where db.ersteller like :ersteller order by db.frist")
	List<UmfrageDB> findByErsteller(@Param("ersteller") String ersteller);
	
	@Query("select db from UmfrageDB db where db.gruppe like :gruppe order by db.frist")
	List<UmfrageDB> findByGruppe(@Param("gruppe") String gruppe);
	
	void deleteByLink(String link);
	
	void deleteByGruppe(String gruppe);
	
	@Query("delete from UmfrageDB where loeschdatum < :timeNow")
	void deleteOutdated(@Param("timeNow") LocalDateTime timeNow);
	
}
