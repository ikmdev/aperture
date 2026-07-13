package dev.ikm.aperture.solor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolorService {

	private static final Logger LOG = LoggerFactory.getLogger(SolorService.class);


	private final IkeService ikeService;
	private final RdfService rdfService;

	@Autowired
	public SolorService(IkeService ikeService, RdfService rdfService) {
		this.ikeService = ikeService;
		this.rdfService = rdfService;
	}

	public String constructSolorGraph(SolorRequest context) {
		return "Solor";
	}

}
