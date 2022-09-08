package it.angelods.ac.webserver.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.angelods.ac.webserver.document.ResourceStream;
import it.angelods.ac.webserver.service.MediaService;

@RestController
@RequestMapping("media/")
public class MediaController {

	@Autowired
	private MediaService mediaService;
	
	@GetMapping(value="{mediaId}", produces = { 
			MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
			MediaType.APPLICATION_PDF_VALUE})
	public byte[] getMedia(@PathVariable("mediaId") String mediaId) {
		try {
			ResourceStream rs = mediaService.getMedia(mediaId);
			return rs.getStream().readAllBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	@PostMapping("/")
	public String saveMedia(MultipartFile file) {
		return mediaService.saveMedia(file);
	}
}
