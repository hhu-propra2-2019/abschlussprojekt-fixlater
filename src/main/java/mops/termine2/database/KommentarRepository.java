package mops.termine2.database;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.KommentarDB;

@Repository
public interface KommentarRepository extends CrudRepository<KommentarDB, Long> {
	
	List<KommentarDB> findByLinkOrderByErstellungsdatumAsc(String link);
	
	@Transactional
	void deleteByLink(String link);
	
}
