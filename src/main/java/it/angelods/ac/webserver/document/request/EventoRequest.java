package it.angelods.ac.webserver.document.request;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class EventoRequest {

	private Instant dataEvento;
	private String settore;
	private String titolo;
	private String ora;
	private double durata;
	private String descrizione;
	private String luogo;
	private List<String> recapiti;
	
}
