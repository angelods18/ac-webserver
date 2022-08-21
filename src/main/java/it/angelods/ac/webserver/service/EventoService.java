package it.angelods.ac.webserver.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import it.angelods.ac.webserver.document.Evento;
import it.angelods.ac.webserver.document.request.EventoRequest;
import it.angelods.ac.webserver.document.response.EventoResponse;

public interface EventoService {

	Evento salvaEvento(EventoRequest eventoRequest, String diocesi);
	
	List<?> getEventi(Map<String,Object> eventoRequest, Pageable pageable);
	
	EventoResponse getEvento(String eventoId);
	
	String salvaLocandina(String eventoId, MultipartFile file);
	
	void allegaLocandina(String evendoId, String id);
	
	void deleteEvento(String eventoId);
}
