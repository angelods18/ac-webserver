package it.angelods.ac.webserver.document.request;

import java.util.List;

import lombok.Data;

@Data
public class IncontroRequest {

	private String diocesi;
	private String titolo;
	private String parrocchia;
	private String descrizione;
	private String obiettivo;
	private String eta;
	private List<String> tags;
	private String settore;
	private long partecipanti;
}
