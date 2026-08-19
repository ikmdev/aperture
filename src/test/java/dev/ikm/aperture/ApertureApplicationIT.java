package dev.ikm.aperture;

import dev.ikm.aperture.capability.*;
import dev.ikm.aperture.search.SearchRequest;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
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
import java.io.StringWriter;
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

	private RestTestClient buildRestTestClient() {
		return RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
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
				3, 3, 3,
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
	void givenSearchRequestWithTVECalculatedRelatedCodes_whenQueried_thenFlattenIntoGraphTurtleOutput() {
		// Given a Search Request with no terminology ids
		/*
			SNOMED CT Concepts:
			128053003 | Deep venous thrombosis (disorder)
			1145072001 | Assessment using Padua Prediction Score for risk of venous thromboembolism (procedure)
			711328005 | At high risk of venous thromboembolism (finding)
			182764009 | Anticoagulant therapy (procedure)

			RxNorm Concepts:
			1364435 | Apixaban (Eliquis)
			30113 | Enoxaparin (Lovenox)
			1114195 | Rivaroxaban (Xarelto)

			LOINC Concepts:
			44434-6 | Risk of venous thromboembolism [Risk] (Generic risk observation)
			39156-5 | Body mass index (BMI) [Ratio] (Crucial scoring criteria for obesity)
			48065-7 | Fibrin D-dimer FEU [Mass/volume] in Platelet poor plasma (Crucial scoring biomarker)
			82810-3 | Current Pregnancy status (Hypercoagulability criteria)
		 */
		SearchRequest searchRequest = new SearchRequest(
				UUID.fromString("05df10d8-88c2-440c-a3c0-a286f14b4cd7"), // Eng Lang with Regular Name
				UUID.fromString("10f727e4-adac-4a94-80f5-00614692aa46"), // Inferred Navigation
				UUID.fromString("1767ad74-0b89-4601-b293-89dc0c51917a"), // Latest on Development Path
				3, 3, 3,
				List.of(128053003L, 1145072001L, 711328005L, 182764009L),
				List.of(1364435, 30113, 1114195),
				List.of("44434-6", "39156-5", "48065-7", "82810-3"),
				List.of());

		// When Search query is posted from client
		String response = postSearchRequest(searchRequest);

		// Then an empty search response string is returned with proper minimal RDF prefixes
		assertThat(response).isNotNull();

		LOG.info("Solor Codes Payload:\n{}", response);
	}

}
