package it.angelods.ac.webserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.angelods.ac.webserver.document.Evento;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String>{

}
