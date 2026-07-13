package dev.ikm.aperture.search;

import dev.ikm.aperture.solor.SolorRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SolorSearchMapper {

	private static final Logger LOG = LoggerFactory.getLogger(SolorSearchMapper.class);


	// Private constructor prevents instantiation
	private SolorSearchMapper() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**     * Maps a web-layer SearchRequest to a domain-layer SolorRequest.     */
	public static SolorRequest toSolorRequest(SearchRequest searchRequest) {
		if (searchRequest == null) {
			return null; // Or throw an exception, depending on your preference
		}

		// Map the fields from the incoming web request to the internal domain request.
		// Assuming SolorRequest does not need the ikeIds based on your earlier work.
		return new SolorRequest(List.of());
	}
}
