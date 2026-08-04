package dev.ikm.aperture.solor;

import dev.ikm.aperture.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

public class SolorMapperService {

	private static final Logger LOG = LoggerFactory.getLogger(SolorMapperService.class);

	// Private constructor prevents instantiation
	private SolorMapperService() {
		LOG.info("SolorMapper()");
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**     * Maps a web-layer SearchRequest to a domain-layer SolorRequest.     */
	public static SolorRequest toSolorRequest(SearchRequest searchRequest) {
		// Check to see if there are any Terminology IDs
		if (searchRequest.isEmpty()) {
			return new SolorRequest(List.of());
		}

		// Convert all Ids from different standards to Tinkar Concept UUIDs



		return new SolorRequest(List.of());
	}
}
