package dev.ikm.aperture.search;

import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.aperture.solor.SolorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

	private final SolorService solorService;

	@Autowired
	public SearchService(SolorService solorService) {
		this.solorService = solorService;
	}

	public String search(SearchRequest searchRequest) {
		SolorRequest solorRequest = SolorSearchMapper.toSolorRequest(searchRequest);
		return solorService.constructSolorGraph(solorRequest);
	}

}
