package it.angelods.ac.webserver.service;

import org.springframework.web.multipart.MultipartFile;

import it.angelods.ac.webserver.document.ResourceStream;

public interface MediaService {

	ResourceStream getMedia(String mediaId);
	
	String saveMedia(MultipartFile file);
}
