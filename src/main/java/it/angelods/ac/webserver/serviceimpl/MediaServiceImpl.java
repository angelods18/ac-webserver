package it.angelods.ac.webserver.serviceimpl;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

import it.angelods.ac.webserver.document.ResourceStream;
import it.angelods.ac.webserver.service.MediaService;

@Service
public class MediaServiceImpl implements MediaService {

	@Autowired
	private GridFsTemplate gridFsTemplate;
	
	@Autowired
	private GridFsOperations operations;
	
	@Override
	public ResourceStream getMedia(String mediaId) {
		// TODO Auto-generated method stub
		GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(mediaId)));
		ResourceStream resource = new ResourceStream();
		if (file == null) {
			return null;
		}
		resource.setTitle(file.getMetadata().get("title").toString());
		resource.setMimeType(file.getMetadata().get("mimeType").toString());
		try {
			resource.setStream(operations.getResource(file).getInputStream());
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Errore in get input stream media");
			return null;
		}
		resource.setFilename(file.getFilename());
		resource.setFileSize(file.getLength());
		return resource;
	}
	
	@Override
	public String saveMedia(MultipartFile file) {
		// TODO Auto-generated method stub
		if(file!=null) {
			String objId="";
			DBObject metadata = new BasicDBObject();
			metadata.put("title", file.getOriginalFilename());
			metadata.put("mimeType", file.getContentType());
			try {
				ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(),
					file.getContentType(), metadata);
				objId = id.toString();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Errore in salvataggio media");
			}
			return objId;
		}else {
			return null;
		}		
	}
}
