package mops.termine2.database;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.UmfrageDB;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
	List<UmfrageDB> findByLink(String link);
	
	List<UmfrageDB> findByErstellerOrderByFristAsc(@Param("ersteller") String ersteller);
	
	List<UmfrageDB> findByGruppeIdOrderByFristAsc(@Param("gruppeId") Long gruppeId);
	
	@Transactional
	void deleteByLink(String link);
	
	@Transactional
	void deleteByGruppeId(Long gruppeId);
	
	@Transactional
	void deleteByLoeschdatumBefore(LocalDateTime timeNow);
	
}
