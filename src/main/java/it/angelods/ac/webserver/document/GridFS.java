package it.angelods.ac.webserver.document;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "fs.files")
@Data
public class GridFS {

	private ObjectId id;
    private String title;
    private String mimeType;
    private String filename;
    private long length;
}
