package it.angelods.ac.webserver.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import it.angelods.ac.webserver.document.Incontro;
import it.angelods.ac.webserver.document.request.IncontroRequest;
import it.angelods.ac.webserver.document.response.IncontroResponse;

public interface IncontroService {

	Incontro salvaIncontro(IncontroRequest request, String diocesi);
	
	Page<?> getIncontri(Map<String,Object> request, Pageable pageable);
	
	Page<?> getTags(Map<String,Object> request, Pageable pageable);
	
	void deleteIncontro(String incontroId);
	
	List<ObjectId> salvaAllegati(MultipartFile[] files);
	
	void allegaFileIncontro(String incontroId, List<ObjectId> idList);
	
	IncontroResponse getIncontro(String incontroId);
}
