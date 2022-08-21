package it.angelods.ac.webserver.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "tags")
@Data
public class Tag {

	@Id
	private String id;
	@Version
	private long version;
	
	private String tag;
	private long counter=0;
	private Instant dateInsert = Instant.now();
}
