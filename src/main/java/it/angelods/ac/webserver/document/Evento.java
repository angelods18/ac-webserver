package it.angelods.ac.webserver.document;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Document(collection = "eventi")
@Data
public class Evento {

	@Id
	private String id;
	@Version
	private long version;
	
	private Instant dataEvento;
	private String diocesi;
	private String settore;
	private String titolo;
	private String ora;
	private double durata;
	private String descrizione;
	private String luogo;
	private List<String> recapiti;
	private String locandina;
}
