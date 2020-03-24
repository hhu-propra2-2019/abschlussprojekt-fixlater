package mops.termine2.database;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mops.termine2.database.entities.KommentarDB;

@Repository
public interface KommentarRepository extends CrudRepository<KommentarDB, Long> {
	
	@Query("select k from KommentarDB k where k.link like :link order by k.erstellungsdatum")
	List<KommentarDB> findByLink(@Param("link") String link);
	
	@Transactional
	@Query("delete from KommentarDB k where k.link like :link")
	void deleteByLink(@Param("link") String link);
	
}
