package it.angelods.ac.webserver.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="diocesi")
@Data
public class Diocesi {

	private String diocesi;
	private String regione;
	private List<Parrocchia> parrocchie = new ArrayList<>();

	@Data
	public class Parrocchia {
		private String citta;
		private String nome;
	}
}
