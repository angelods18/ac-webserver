package it.angelods.ac.webserver.document.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.Data;

@Data
public class EventoResponse {

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
	private List<AllegatoResponse> locandina;
}
