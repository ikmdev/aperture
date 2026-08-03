package dev.ikm.aperture;

import dev.ikm.aperture.capability.*;
import dev.ikm.aperture.search.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ApertureApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApertureApplicationTests {

	@LocalServerPort
	private int port;

	@Value("${app.api.key}")
	private String apiKey;

	@Value("${app.database.directory}")
	private String databaseDirectory;

	private RestTestClient buildRestTestClient() {
		return RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.defaultHeader("X-API-KEY", apiKey)
				.build();
	}


	@Test
	void contextLoads() {
	}

	@Test
	void searchSmokeTest() {
		SearchRequest searchRequest = new SearchRequest(List.of(), List.of(), List.of(), List.of());

		// Create REST Client
		RestTestClient client = buildRestTestClient();

		// Now you can make requests without mentioning the header again
		client.post()
				.uri("/api/search")
				.contentType(MediaType.APPLICATION_JSON)
				.body(searchRequest)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void capabilityTestForKnowledgeRetrieval() {
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

}
