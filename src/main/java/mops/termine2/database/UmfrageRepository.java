package mops.termine2.database;

import mops.termine2.database.entities.UmfrageDB;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UmfrageRepository extends CrudRepository<UmfrageDB, Long> {
	
	List<UmfrageDB> findByLink(String link);
	
	List<UmfrageDB> findByErstellerOrderByFristAsc(String ersteller);
	
	List<UmfrageDB> findByGruppeIdOrderByFristAsc(String gruppeId);
	
	@Transactional
	void deleteByLink(String link);
	
	@Transactional
	void deleteByGruppeId(String gruppeId);
	
	@Transactional
	void deleteByLoeschdatumBefore(LocalDateTime timeNow);
	
	UmfrageDB findByLinkAndAuswahlmoeglichkeit(String link, String vorschlag);
	
	List<UmfrageDB> findByFristBeforeAndErgebnisIsNull(LocalDateTime timeNow);
	
	List<UmfrageDB> findByLoeschdatumBefore(LocalDateTime timeNow);
	
}
