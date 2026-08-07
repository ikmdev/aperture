package dev.ikm.aperture;

import dev.ikm.aperture.capability.*;
import dev.ikm.aperture.search.SearchRequest;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ApertureApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApertureApplicationIT {

	private static final Logger LOG = LoggerFactory.getLogger(ApertureApplicationIT.class);

	private static final ObjectMapper MAPPER = JsonMapper.builder()
			.enable(SerializationFeature.INDENT_OUTPUT)
			.build();

	@LocalServerPort
	private int port;

	@Value("${app.api.key}")
	private String apiKey;

	private RestTestClient buildRestTestClient() {
		return RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.defaultHeader("X-API-KEY", apiKey)
				.build();
	}

	private String postSearchRequest(SearchRequest request) {
		return buildRestTestClient()
				.post()
				.uri("/api/search")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.parseMediaType("text/turtle"))
				.body(request)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith("text/turtle")
				.returnResult(String.class)
				.getResponseBody();
	}


	@Test
	void contextLoads() {
		// Checking to see that the Spring Boot Test context has loaded
	}

	@Test
	void whenCapabilityStatementQueried_thenMinimumViableCoordinateSelectionIsPresent() {
		// Given a REST client with a connection to Aperture
		RestTestClient client = buildRestTestClient();

		// When the client requests information regarding the capabilities with respect to knowledge retrieval that
		// that Aperture can support.
		CapabilityStatement response = client.get()
				.uri("/api/capability")
				.exchange()
				.expectStatus().isOk()
				.returnResult(CapabilityStatement.class)
				.getResponseBody();

		LOG.info("Capability Statement Response payload:\n{}", MAPPER.writeValueAsString(response));

		// Then a capability statement is returned that describes all available configurations of supported
		// languages, navigations, and stamps capabilities.
		assertThat(response).isNotNull();

		// Language Coordinates at least contain US_ENG_REG
		assertThat(response).hasFieldOrProperty("languageCoordinates");
		assertThat(response.languageCoordinates().stream().filter(coordinate ->
				coordinate.name().equals("US English Language Regular Name") &&
						coordinate.uuids().contains(UUID.fromString("05df10d8-88c2-440c-a3c0-a286f14b4cd7"))))
				.isNotEmpty();

		// Navigation Coordinates at least contain INFERRED
		assertThat(response).hasFieldOrProperty("navigationCoordinates");
		assertThat(response.navigationCoordinates().stream().filter(coordinate ->
				coordinate.name().equals("Inferred Navigation") &&
						coordinate.uuids().contains(UUID.fromString("10f727e4-adac-4a94-80f5-00614692aa46"))))
				.isNotEmpty();

		// Stamp Coordinates at least contain DEV_LATEST
		assertThat(response).hasFieldOrProperty("stampCoordinates");
		assertThat(response.stampCoordinates().stream().filter(coordinate ->
				coordinate.name().equals("Latest on the Development Path") &&
						coordinate.uuids().contains(UUID.fromString("1767ad74-0b89-4601-b293-89dc0c51917a"))))
				.isNotEmpty();
	}

	@Test
	void givenSearchRequestWithNoTerminologyIdentifiers_whenSearchQueried_thenEmptySearchResponseWithRDFPrefixesIsReturned() {
		// Given a Search Request with no terminology ids
		SearchRequest searchRequest = new SearchRequest(
				UUID.fromString("05df10d8-88c2-440c-a3c0-a286f14b4cd7"), // Eng Lang with Regular Name
				UUID.fromString("10f727e4-adac-4a94-80f5-00614692aa46"), // Inferred Navigation
				UUID.fromString("1767ad74-0b89-4601-b293-89dc0c51917a"), // Latest on Development Path
				List.of(), List.of(), List.of(), List.of());

		// When Search query is posted from client
		String response = postSearchRequest(searchRequest);

		LOG.info("No Terminology Identifiers RDF Graph Response payload:\n{}", response);

		// Then an empty search response string is returned with proper minimal RDF prefixes
		assertThat(response).isNotNull();

		// Check for baseline prefixes
		long nonPrefixLines = response.lines()
				.filter(line -> !line.trim().isEmpty() && !line.startsWith("@prefix"))
				.count();
		assertThat(nonPrefixLines).isEqualTo(3);
	}

	@Test
	void givenSearchRequestWithDeepVeinThrombosisSnomedConceptIdentifier_whenSearchQueried_thenSearchResponseWithRDFGraphIsReturned() {
		// Given a Search Request with no terminology ids
		SearchRequest searchRequest = new SearchRequest(
				UUID.fromString("05df10d8-88c2-440c-a3c0-a286f14b4cd7"), // Eng Lang with Regular Name
				UUID.fromString("10f727e4-adac-4a94-80f5-00614692aa46"), // Inferred Navigation
				UUID.fromString("1767ad74-0b89-4601-b293-89dc0c51917a"), // Latest on Development Path
				List.of(128053003L),
				List.of(), List.of(), List.of());

		// When Search query is posted from client
		String response = postSearchRequest(searchRequest);

		// Then an empty search response string is returned with proper minimal RDF prefixes
		assertThat(response).isNotNull();

		LOG.info("DVT Snomed CT Identifier RDF Graph Response payload:\n{}", response);

		// Parse the Turtle string back into a Queryable Graph Model
		Model responseModel = ModelFactory.createDefaultModel();
		responseModel.read(new StringReader(response), null, "TURTLE");

		// 1. Simple Property Check: Ensure the main concept has the correct label
		String conceptUri = "https://www.ikm.dev/solor/41bfe458-c5fa-54af-8889-2465e8639d95";
		String label = responseModel.getResource(conceptUri)
				.getProperty(RDFS.label).getString();
		assertThat(label).isEqualTo("Deep venous thrombosis");

		// 2. Complex Graph Shape Check: Use SPARQL ASK to find nested blank nodes
		// This completely ignores the order/formatting of the RDF strings
		String askIdentifierShape = """
				PREFIX solor: <https://www.ikm.dev/solor/>
				ASK {
					solor:41bfe458-c5fa-54af-8889-2465e8639d95 solor:has_identifier ?idNode .
					?idNode solor:has_identifier_value "128053003" .
					?idNode solor:has_identifier_system solor:ab9a0e0a-6359-5462-859c-96c3d4ef2341 .
				}
				""";
		try (QueryExecution qExec = QueryExecutionFactory.create(askIdentifierShape, responseModel)) {
			assertThat(qExec.execAsk()).as("Should contain the SCTID paired with its system").isTrue();
		}

		// 3. Another SPARQL ASK for the Fully Qualified Name description shape
		String askDescriptionShape = """
				PREFIX solor: <https://www.ikm.dev/solor/>
				ASK {
					solor:41bfe458-c5fa-54af-8889-2465e8639d95 solor:has_description ?descNode .
					?descNode solor:has_fully_qualified_name "Deep venous thrombosis (disorder)"@en-US .
				}
				""";
		try (QueryExecution qExec = QueryExecutionFactory.create(askDescriptionShape, responseModel)) {
			assertThat(qExec.execAsk()).as("Should contain the EN-US fully qualified name").isTrue();
		}

		// 4. SPARQL ASK for multiple Synonyms and their specific Case Sensitivity metadata
		String askSynonymsShape = """
				PREFIX solor: <https://www.ikm.dev/solor/>
				ASK {
					# The main concept
					solor:41bfe458-c5fa-54af-8889-2465e8639d95 
					        solor:has_description ?descNode1 , 
					                              ?descNode2 , 
					                              ?descNode3 .
					
					# Synonym 1: 'Deep vein thrombosis' (Case insensitive)
					?descNode1 solor:has_synonym "Deep vein thrombosis"@en-US ;
					           solor:has_case_sensitivity solor:ecea41a2-f596-3d98-99d1-771b667e55b8 .
					
					# Synonym 2: 'DVT' (Case sensitive)
					?descNode2 solor:has_synonym "DVT"@en-US ;
					           solor:has_case_sensitivity solor:0def37bc-7e1b-384b-a6a3-3e3ceee9c52e .
					           
					# Synonym 3: 'DVT - Deep vein thrombosis' (Case sensitive)
					?descNode3 solor:has_synonym "DVT - Deep vein thrombosis"@en-US ;
					           solor:has_case_sensitivity solor:0def37bc-7e1b-384b-a6a3-3e3ceee9c52e .
				}
				""";
		try (QueryExecution qExec = QueryExecutionFactory.create(askSynonymsShape, responseModel)) {
			assertThat(qExec.execAsk()).as("Should contain specific synonyms paired with correct case sensitivity").isTrue();
		}

	}

}
