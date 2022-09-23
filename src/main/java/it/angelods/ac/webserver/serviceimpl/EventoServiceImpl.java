package it.angelods.ac.webserver.serviceimpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import it.angelods.ac.webserver.document.Evento;
import it.angelods.ac.webserver.document.Incontro;
import it.angelods.ac.webserver.document.request.EventoRequest;
import it.angelods.ac.webserver.document.response.EventoResponse;
import it.angelods.ac.webserver.repositories.EventoRepository;
import it.angelods.ac.webserver.service.EventoService;

@Service
public class EventoServiceImpl implements EventoService {
	
	private static final String EVENTI = "eventi";
	private static final String SETTORE = "settore";
	private static final String TITOLO = "titolo";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String LUOGO = "luogo";
	private static final String DESCRIZIONE = "descrizione";
	private static final String DATA_EVENTO = "dataEvento";
	
	@Autowired
	private EventoRepository eventoRepository;
	
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	
	@Autowired
	public EventoServiceImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<?> getEventi(Map<String, Object> eventoRequest, Pageable pageable) {
		// TODO Auto-generated method stub
		Criteria criteria = new Criteria();
		if(eventoRequest.containsKey(SETTORE)) {
			criteria = Criteria.where(SETTORE).is(eventoRequest.get(SETTORE).toString());
		}
		Document projectOperation = new Document("$project", 
			    new Document("month", 
			    	    new Document("$month", "$dataEvento"))
			    	            .append("settore", "$settore")
			    	            .append("titolo", "$titolo"));
		Aggregation aggregation = null;
		if(eventoRequest.containsKey(MONTH)) {
			criteria = criteria.and(MONTH).is(Long.valueOf(eventoRequest.get(MONTH).toString()));
			AggregationOperation match = Aggregation.match(criteria);
			
			AggregationOperation project = projectMonth();
			
			aggregation = Aggregation.newAggregation(project, match);
		}else {
			criteria = criteria.and(DATA_EVENTO).gte(Instant.now().minus(1, ChronoUnit.DAYS ));
			AggregationOperation match = Aggregation.match(criteria);
			
			AggregationOperation project = projectMonth();
			AggregationOperation sort = Aggregation.sort(Sort.by(Direction.ASC, DATA_EVENTO));
			
			aggregation = Aggregation.newAggregation(project, match, sort);
		}
		
		
		// usa altri filtri
		List<?> eventi = mongoTemplate.aggregate(aggregation, EVENTI, Object.class).getMappedResults();
		return eventi;
	}
		
	@Override
	public Evento salvaEvento(EventoRequest eventoRequest, String diocesi) {
		// TODO Auto-generated method stub
		Evento evento = new Evento();
		BeanUtils.copyProperties(eventoRequest, evento);
		evento.setDiocesi(diocesi);
		return eventoRepository.save(evento);
	}
	
	@Override
	public String salvaLocandina(String eventoId, MultipartFile file) {
		// TODO Auto-generated method stub
		if(file!=null) {
			String objId="";
			DBObject metadata = new BasicDBObject();
			metadata.put("title", file.getName());
			metadata.put("mimeType", file.getContentType());
			try {
				ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getName(),
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
	
	@Override
	@Async
	public void allegaLocandina(String evendoId, String id) {
		// TODO Auto-generated method stub
		Optional<Evento> evento = eventoRepository.findById(evendoId);
		if(evento.isEmpty()) {
			removeFilesFromGridFS(Arrays.asList(id));
		}else {
			Query query = Query.query(Criteria.where("_id").is(evendoId));
			Update update = new Update();
			update.set("locandina", id);
			mongoTemplate.updateFirst(query, update, EVENTI);
		}
		
	}
	
	@Override
	public EventoResponse getEvento(String eventoId) {
		// TODO Auto-generated method stub
		Criteria criteria = Criteria.where("_id").is(eventoId);
		//Document lookup = lookupMediaAggregation();
		
		AggregationOperation match = Aggregation.match(criteria);
		List<AggregationOperation> aggregationList = new ArrayList<>(Arrays.asList(match));
		Aggregation aggregation = Aggregation.newAggregation(aggregationList);
		return mongoTemplate.aggregate(aggregation, EVENTI, EventoResponse.class).getUniqueMappedResult();
//		return incontroRepository.findById(incontroId).orElse(null);
	}
	
	@Override
	public void deleteEvento(String eventoId) {
		// TODO Auto-generated method stub
		Optional<Evento> evento = eventoRepository.findById(eventoId);
		if(evento.isPresent()) {
			if(evento.get().getLocandina()!=null) {
				removeFilesFromGridFS(Arrays.asList(evento.get().getLocandina()));
			}
			eventoRepository.delete(evento.get());
		}
	}
	
	private Document lookupMediaAggregation() {
		Document lookup = new Document("$lookup", 
				new Document("from", "fs.files")
            		.append("let", 
					    new Document("fileId", 
					    new Document("$toObjectId", "$_id")))
			        .append("pipeline", Arrays.asList(new Document("$match", 
		                new Document("$expr", Arrays.asList(new Document("locandina", "$$fileId")))), 
		                new Document("$project", 
	                		new Document("filename", 1L))))
		            .append("as", "locandina"));
		return lookup;
	}
	
	private void removeFilesFromGridFS(List<String> fileIds) {
		fileIds.stream().forEach(ids -> {
			gridFsTemplate.delete(new Query(Criteria.where("_id").is(ids)));
		});
	}
	
	private AggregationOperation projectMonth() {
		return Aggregation.project("_id")
		.andExpression("toString(_id)").as("id")
		.andExpression("month(dataEvento)").as(MONTH)
		.andExpression("dayOfMonth(dataEvento)").as(DAY)
		.and(SETTORE).as(SETTORE).and(TITOLO).as(TITOLO)
		.and(LUOGO).as(LUOGO).and(DESCRIZIONE).as(DESCRIZIONE)
		.and(DATA_EVENTO).as(DATA_EVENTO);
	}
}
