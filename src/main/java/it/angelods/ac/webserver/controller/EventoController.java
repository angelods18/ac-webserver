package it.angelods.ac.webserver.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.angelods.ac.webserver.document.Evento;
import it.angelods.ac.webserver.document.request.EventoRequest;
import it.angelods.ac.webserver.document.response.EventoResponse;
import it.angelods.ac.webserver.service.DiocesiService;
import it.angelods.ac.webserver.service.EventoService;

@RestController
@RequestMapping("evento/")
public class EventoController {

	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private DiocesiService diocesiService;
	
	@GetMapping("/ping")
	public String ping() {
		return "PONG";
	}
	
	@PostMapping("/salva-evento")
	public Evento salvaEvento(@RequestBody EventoRequest eventoRequest, HttpServletRequest request){
		System.out.println(eventoRequest);
		String diocesi="";
		if(request.getHeader("Diocesi")!=null) {
			diocesi = request.getHeader("Diocesi");
			diocesiService.saveDiocesi(diocesi);
		}

		return eventoService.salvaEvento(eventoRequest, diocesi);
	}
	
	@PostMapping("{eventoId}/salva-locandina")
	public void salvaLocandina(@PathVariable("eventoId") String eventoId, @RequestParam("file") MultipartFile file) {
		String id = eventoService.salvaLocandina(eventoId, file);
		eventoService.allegaLocandina(eventoId, id);
	}
	
	/**
	 * 
	 * @param eventoRequest: { "settore": "GVN", "month":5}
	 * @param pageable
	 * @return
	 */
	@GetMapping("eventi")
	public List<?> getEventi(@RequestParam Map<String,Object> eventoRequest,  @PageableDefault(page=0, size = 10) Pageable pageable ) {
		return eventoService.getEventi(eventoRequest, pageable);
	}
	
	@GetMapping("{eventoId}")
	public EventoResponse getEvento(@PathVariable("eventoId") String eventoId) {
		return eventoService.getEvento(eventoId);
	}
	
	@DeleteMapping("{eventoId}")
	public void deleteEvento(@PathVariable("eventoId") String eventoId) {
		eventoService.deleteEvento(eventoId);
	}
}
