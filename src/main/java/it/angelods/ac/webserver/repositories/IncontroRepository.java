package it.angelods.ac.webserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.angelods.ac.webserver.document.Incontro;

@Repository
public interface IncontroRepository extends MongoRepository<Incontro, String>{

}
