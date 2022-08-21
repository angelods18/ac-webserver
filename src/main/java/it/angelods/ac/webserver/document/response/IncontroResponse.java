package it.angelods.ac.webserver.document.response;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.Data;

@Data
public class IncontroResponse {

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
	private List<AllegatoResponse> allegati;
}
