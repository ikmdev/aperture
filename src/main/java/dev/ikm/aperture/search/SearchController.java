package dev.ikm.aperture.search;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SearchController {

	private final SearchService searchService;

	@Autowired
	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@PostMapping("/search")
	public ResponseEntity<String> searchForSolorKnowledge(@Valid @RequestBody SearchRequest request) {
		// Search for subgraph based on search request parameters
		String rdfTurtleGraph = searchService.search(request);

		// Setup appropriate header metadata to ensure response is proper RDF Turtle data
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=search-results.ttl");
		headers.add(HttpHeaders.CONTENT_TYPE, "text/turtle");

		return ResponseEntity.ok()
				.headers(headers)
				.body(rdfTurtleGraph);
	}

}
