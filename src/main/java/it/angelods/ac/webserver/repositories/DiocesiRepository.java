package it.angelods.ac.webserver.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.angelods.ac.webserver.document.Diocesi;

@Repository
public interface DiocesiRepository extends MongoRepository<Diocesi,String>{

	Optional<Diocesi> findByDiocesi(String diocesi);
	
	Optional<Diocesi> findByParrocchieNome(String parrocchia);
}
