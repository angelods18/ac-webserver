package it.angelods.ac.webserver.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
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

import it.angelods.ac.webserver.document.Incontro;
import it.angelods.ac.webserver.document.request.IncontroRequest;
import it.angelods.ac.webserver.document.response.IncontroResponse;
import it.angelods.ac.webserver.service.DiocesiService;
import it.angelods.ac.webserver.service.IncontroService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("incontro/")
@Log4j2
public class IncontroController {

	@Autowired
	private IncontroService incontroService;
	
	@Autowired
	private DiocesiService diocesiService;
	
	@GetMapping("/ping")
	public String ping() {
		return "PONG";
	}
	
	/**
	 * 
	 * @param incontroRequest
	 * @return Incontro
	 */
	@PostMapping("/salva-incontro")
	public Incontro salvaIncontro(@RequestBody IncontroRequest incontroRequest, HttpServletRequest request) {
		String diocesi="";
		if(request.getHeader("Diocesi")!=null) {
			diocesi = request.getHeader("Diocesi");
			diocesiService.saveDiocesi(diocesi);
		}
		System.out.println("Salva incontro: "+ incontroRequest);
		return incontroService.salvaIncontro(incontroRequest, diocesi);
	}
	
	/**
	 * @param settore={settore}
	 * @return lista paginata di incontri
	 */
	@GetMapping("/incontri")
	public Page<?> getAllIncontri(@RequestParam Map<String,Object> incontroRequest, @PageableDefault(page=0, size = 10) Pageable pageable){
		return incontroService.getIncontri(incontroRequest, pageable);
	}
	
	/**
	 * 
	 * @param tagRequest - da definire
	 * @param pageable
	 * @return lista paginata di parole chiave ordinate per counter decrescente
	 */
	@GetMapping("/tags")
	public Page<?> getTags(@RequestParam Map<String,Object> tagRequest, @PageableDefault(page=0, size = 10) Pageable pageable){
		return incontroService.getTags(tagRequest, pageable);
	}
	
	@GetMapping("{incontroId}")
	public IncontroResponse getIncontro(@PathVariable("incontroId") String incontroId) {
		return incontroService.getIncontro(incontroId);
	}
	
	@DeleteMapping("{incontroId}")
	public void deleteIncontro(@PathVariable("incontroId") String incontroId) {
		incontroService.deleteIncontro(incontroId);
	}
	
	@PostMapping("/{incontroId}/salva-allegati")
	public void salvaAllegati(@PathVariable("incontroId") String incontroId, @RequestParam("files") MultipartFile[] files) {
		List<ObjectId> idList = incontroService.salvaAllegati(files);
		incontroService.allegaFileIncontro(incontroId, idList);
	}
}
