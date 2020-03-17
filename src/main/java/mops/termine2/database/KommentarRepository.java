package mops.termine2.database;

import mops.termine2.database.entities.KommentarDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KommentarRepository extends CrudRepository<KommentarDB, Long> {
	
	@Query("select k from KommentarDB k where k.link like :link order by k.erstellungsdatum")
	List<KommentarDB> findByLink(@Param("link") String link);
	
	@Query("delete from KommentarDB k where k.link like :link")
	void deleteByLink(@Param("link") String link);
	
}
