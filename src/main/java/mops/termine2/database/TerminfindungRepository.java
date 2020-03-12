package mops.termine2.database;

import mops.termine2.database.entities.TerminfindungDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminfindungRepository extends CrudRepository<TerminfindungDB, Long> {
	
	List<TerminfindungDB> findByLink(String link);
	
	@Query("select distinct db.link,db.titel,db.ersteller,db.loeschdatum,db.frist,db.gruppe,db.ort,db.beschreibung"
			+ " from TerminfindungDB db "
			+ "where db.ersteller like:ersteller  order by db.frist")
	List<TerminfindungDB> findByErstellerOhneTermine(@Param("ersteller") String ersteller);
	
	@Query("select distinct db.link,db.titel,db.ersteller,db.loeschdatum,db.frist,db.gruppe,db.ort,db.beschreibung"
			+ " from TerminfindungDB db "
			+ "where db.gruppe like:gruppe  order by db.frist")
	List<TerminfindungDB> findByGruppeOhneTermine(@Param("gruppe") String gruppe);
	
}
