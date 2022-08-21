package it.angelods.ac.webserver.document;

import java.io.InputStream;

import lombok.Data;

@Data
public class ResourceStream {

	private String title;
    private String description;
    private String mimeType;
    private InputStream stream;
    private String filename;
    private Long fileSize;
}
