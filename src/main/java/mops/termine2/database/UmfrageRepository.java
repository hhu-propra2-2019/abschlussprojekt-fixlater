package mops.termine2.database;

import mops.termine2.database.entities.UmfrageDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
	List<UmfrageDB> findByLink(String link);
	
	@Query("select db from UmfrageDB db where db.ersteller like :ersteller order by db.frist")
	List<UmfrageDB> findByErsteller(@Param("ersteller") String ersteller);
	
	@Query("select db from UmfrageDB db where db.gruppeId = :gruppeId order by db.frist")
	List<UmfrageDB> findByGruppeId(@Param("gruppeId") Long gruppeId);
	
	@Transactional
	@Query("delete from UmfrageDB db where db.link like : link")
	void deleteByLink(@Param("link") String link);
	
	@Transactional
	void deleteByGruppeId(Long gruppeId);
	
	@Transactional
	@Query("delete from UmfrageDB where loeschdatum < :timeNow")
	void deleteOutdated(@Param("timeNow") LocalDateTime timeNow);
	
	UmfrageDB findByLinkAndAuswahlmoeglichkeit(String link, String vorschlag);
	
}
