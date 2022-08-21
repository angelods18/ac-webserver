package it.angelods.ac.webserver.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "incontro")
@Data
public class Incontro {

	@Id
	private String id;
	@Version
	private long version;
	private String diocesi;
	private String titolo;
	private String parrocchia;
	private String descrizione;
	private String obiettivo;
	private String eta;
	private List<String> tags;
	private String settore;
	private long partecipanti;
	private List<String> allegati;
}
