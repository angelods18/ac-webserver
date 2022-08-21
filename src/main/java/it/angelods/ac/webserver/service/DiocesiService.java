package it.angelods.ac.webserver.service;

import it.angelods.ac.webserver.document.Diocesi;

public interface DiocesiService {

	Diocesi saveDiocesi(String diocesi);
	
	void saveParrocchia(String diocesi, String parrocchia);
}
