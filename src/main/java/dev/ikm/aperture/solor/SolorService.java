package dev.ikm.aperture.solor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolorService {

	private static final Logger LOG = LoggerFactory.getLogger(SolorService.class);

	private final RdfService rdfService;

	@Autowired
	public SolorService(RdfService rdfService) {
		this.rdfService = rdfService;
	}

	public String constructSolorGraph(SolorRequest solorRequest) {
		if (solorRequest.conceptUUIDs().isEmpty()) {
			LOG.info("No concept UUIDs provided, returning empty Solor graph");
			return rdfService.generateEmptySolorGraph();
		} else {
			return rdfService.generateSolorGraph(solorRequest);
		}
	}

}
