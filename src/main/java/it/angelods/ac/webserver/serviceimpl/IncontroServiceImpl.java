package it.angelods.ac.webserver.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import it.angelods.ac.webserver.document.Incontro;
import it.angelods.ac.webserver.document.Tag;
import it.angelods.ac.webserver.document.request.IncontroRequest;
import it.angelods.ac.webserver.document.response.IncontroResponse;
import it.angelods.ac.webserver.repositories.IncontroRepository;
import it.angelods.ac.webserver.repositories.TagRepository;
import it.angelods.ac.webserver.service.IncontroService;

@Service
public class IncontroServiceImpl implements IncontroService{

	private static final String INCONTRO = "incontro";
	private static final String PARROCCHIA = "parrocchia";
	private static final String SETTORE = "settore";
	private static final String TAGS = "tags";
	private static final String TAG_COUNTER= "counter";
	private static final String VERSION = "version";
	private static final String ALLEGATI ="allegati";
	private static final String DESCRIZIONE = "descrizione";
	private static final String OBIETTIVO = "obiettivo";
	private static final String ETA = "eta";
	private static final String TITOLO = "titolo";
	private static final String PARTECIPANTI = "partecipanti";
	private static final String SEARCH_WORD="search";
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private IncontroRepository incontroRepository;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	
	private MongoTemplate mongoTemplate;
	
	@Autowired
	public IncontroServiceImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	@Transactional
	public Incontro salvaIncontro(IncontroRequest request, String diocesi) {
		// TODO Auto-generated method stub
		if(request.getParrocchia()!=null) {
			String parrocchia = request.getParrocchia();
			Map<String,Object> parr = new HashMap<>();
			parr.put(PARROCCHIA, parrocchia);
			mongoTemplate.save(parr, PARROCCHIA);
		}
		if(request.getTags()!=null && !request.getTags().isEmpty() ) {
			handleTag(request.getTags());
		}
		Incontro incontro = new Incontro();
		BeanUtils.copyProperties(request, incontro);
		incontro.setDiocesi(diocesi);
		return incontroRepository.save(incontro);
	}
	
	private void handleTag(List<String> tags) {
		if(tags.size()>0) {
			for (String tag : tags) {
				Optional<Tag> t = tagRepository.findByTag(tag);
				if(t.isEmpty()) {
					Tag newTag = new Tag();
					mongoTemplate.save(newTag, TAGS);
				}else {
					Query query = Query.query(Criteria.where("_id").is(t.get().getId()));
					
					Update update = new Update();
					update.set(TAG_COUNTER, t.get().getCounter()+1).inc(VERSION, 0);
					mongoTemplate.updateFirst(query, update, TAGS);
				}
				
			}
		}
	}
	
	@Override
	public Page<?> getIncontri(Map<String, Object> request, Pageable pageable) {
		// TODO Auto-generated method stub
		Criteria criteria = Criteria.where(SETTORE).is(request.get("settore").toString());
		// usa altri filtri
		manageCriteriaIncontro(criteria, request);
		List<Incontro> incontri = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)), INCONTRO, Incontro.class).getMappedResults();
		return new PageImpl<>(incontri, pageable, incontri.size());
	}
	
	@Override
	public Page<?> getTags(Map<String, Object> request, Pageable pageable) {
		// TODO Auto-generated method stub
		List<Tag> tags = new ArrayList<>();
		String tagFilter = request.get("tag")!=null ? request.get("tag").toString() : "";
		Pattern pattern = Pattern.compile(tagFilter, Pattern.CASE_INSENSITIVE);
		Criteria crit = Criteria.where("tag").is(pattern);
		AggregationOperation match = Aggregation.match(crit);
		AggregationOperation sortTags = Aggregation.sort(Direction.DESC,"counter");
		Aggregation aggregation = Aggregation.newAggregation(match, sortTags);
		tags = mongoTemplate.aggregate(aggregation, TAGS, Tag.class).getMappedResults();
		
		return new PageImpl<>(tags, pageable, tags.size());
	}
	
	@Override
	public IncontroResponse getIncontro(String incontroId) {
		// TODO Auto-generated method stub
		Criteria criteria = Criteria.where("_id").is(incontroId);
		Document lookup = lookupMediaAggregation();

		AggregationOperation match = Aggregation.match(criteria);
		Document unwindPreserveEmptyArray = new Document("$unwind", 
				new Document("path","$allegati")
				.append("preserveNullAndEmptyArrays",true));
		Document addField = new Document("$addFields", new Document("fileId", new Document("$toObjectId","$allegati")));
		AggregationOperation group = groupIncontri();
		List<AggregationOperation> aggregationList = 
				new ArrayList<>(Arrays.asList(match, u -> unwindPreserveEmptyArray, a->addField, l->lookup, group));
				
		Aggregation aggregation = Aggregation.newAggregation(aggregationList);
		return mongoTemplate.aggregate(aggregation, INCONTRO, IncontroResponse.class).getUniqueMappedResult();
//		return incontroRepository.findById(incontroId).orElse(null);
	}
	
	@Override
	public void deleteIncontro(String incontroId) {
		// TODO Auto-generated method stub
		Optional<Incontro> incontro = incontroRepository.findById(incontroId);
		if(incontro.isPresent()) {
			if(incontro.get().getAllegati()!=null) {
				removeFilesFromGridFSString(incontro.get().getAllegati());
			}
			incontroRepository.delete(incontro.get());
		}
	}
	
	@Override
	public List<ObjectId> salvaAllegati(MultipartFile[] files) {
		// TODO Auto-generated method stub

		if(files!=null && files.length>0) {
			List<ObjectId> idList = new ArrayList<>();
			Arrays.asList(files).stream().forEach(f -> {
				DBObject metaData = new BasicDBObject();
				metaData.put("title", f.getName());
				metaData.put("mimeType", f.getContentType());
				try {
					ObjectId id = gridFsTemplate.store(f.getInputStream(), f.getName(),
							f.getContentType(), metaData);
					idList.add(id);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Errore in salvataggio media");
				}
			});
			
			return idList;
		}else {
			return new ArrayList<>();
		}
		
	}
	
	@Override
	@Async
	public void allegaFileIncontro(String incontroId, List<ObjectId> idList) {
		// TODO Auto-generated method stub
		Optional<Incontro> incontro = incontroRepository.findById(incontroId);
		if(incontro.isEmpty()) {
			removeFilesFromGridFS(idList);
		}else {
			Query query = Query.query(Criteria.where("_id").is(incontroId));
			
			Update update = new Update();
			List<String> ids = incontro.get().getAllegati();
			if(ids==null) {
				ids=new ArrayList<>();
			}
			for (ObjectId id : idList) {
				ids.add(id.toString());
			}
			
			update.set("allegati", ids);
			mongoTemplate.updateFirst(query, update, INCONTRO);
		}
	}
	
	private Document lookupMediaAggregation() {
		Document lookup = new Document("$lookup", 
				new Document("from", "fs.files")
            		.append("localField", "fileId")
            		.append("foreignField", "_id")
		            .append("as", "allegati"));
		return lookup;
	}
	
	private void manageCriteriaIncontro(Criteria criteria, Map<String,Object> request) {
		if(request.containsKey(SEARCH_WORD)) {
			Pattern pattern = Pattern.compile(request.get(SEARCH_WORD).toString(), Pattern.CASE_INSENSITIVE);
			Criteria filtroRicerca = new Criteria().orOperator(
					Criteria.where(TITOLO).is(pattern),
					Criteria.where(TAGS).is(pattern)
					);
			criteria.andOperator(filtroRicerca);
		}
	}
	
	private AggregationOperation groupIncontri() {
		return Aggregation.group("_id").first(TITOLO).as(TITOLO).first(TAGS).as(TAGS).first(ETA).as(ETA)
				.first(PARROCCHIA).as(PARROCCHIA).first(SETTORE).as(SETTORE).first(OBIETTIVO).as(OBIETTIVO)
				.first(ALLEGATI).as(ALLEGATI).first(DESCRIZIONE).as(DESCRIZIONE).first(PARTECIPANTI).as(PARTECIPANTI);
	}
		
	private void removeFilesFromGridFS(List<ObjectId> fileIds) {
		fileIds.stream().forEach(ids -> {
			gridFsTemplate.delete(new Query(Criteria.where("_id").is(ids)));
		});
	}
	
	private void removeFilesFromGridFSString(List<String> fileIds) {
		fileIds.stream().forEach(ids -> {
			gridFsTemplate.delete(new Query(Criteria.where("_id").is(ids)));
		});
	}
}
