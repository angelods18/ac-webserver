package it.angelods.ac.webserver.serviceimpl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.angelods.ac.webserver.document.Diocesi;
import it.angelods.ac.webserver.repositories.DiocesiRepository;
import it.angelods.ac.webserver.service.DiocesiService;

@Service
public class DiocesiServiceImpl implements DiocesiService {

	@Autowired
	DiocesiRepository diocesiRepository;
	
	@Override
	public Diocesi saveDiocesi(String diocesi) {
		// TODO Auto-generated method stub
		Optional<Diocesi> d = diocesiRepository.findByDiocesi(diocesi);
		if(d.isEmpty()) {
			Diocesi newDiocesi = new Diocesi();
			newDiocesi.setDiocesi(diocesi);
			newDiocesi.setParrocchie(new ArrayList<>());
			return diocesiRepository.save(newDiocesi);
		}else {
			return d.get();
		}
	}
	
	
	@Override
	public void saveParrocchia(String diocesi, String parrocchia) {
		// TODO Auto-generated method stub
		Diocesi d = saveDiocesi(diocesi);
		// to be continued
	}
}
