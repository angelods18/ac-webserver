package it.angelods.ac.webserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.angelods.ac.webserver.document.GridFS;

public interface GridFSRepository extends MongoRepository<GridFS, String>{

}
