package it.angelods.ac.webserver.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.angelods.ac.webserver.document.Tag;

@Repository
public interface TagRepository extends MongoRepository<Tag, String>{

	Optional<Tag> findByTag(String tag);
	
	List<Tag> findAllByOrderByCounterDesc();
}
