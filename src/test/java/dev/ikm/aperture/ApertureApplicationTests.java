package dev.ikm.aperture;

import dev.ikm.aperture.capability.CapabilityStatement;
import dev.ikm.aperture.search.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@SpringBootTest(classes = ApertureApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApertureApplicationTests {

	@LocalServerPort
	private int port;

	@Value("${app.api.key}")
	private String apiKey;

	@Value("${app.database.directory}")
	private String databaseDirectory;


	Path ikeDB = Paths.get(System.getProperty("user.dir"), "target").resolve("data").resolve("solor-export-1.0.0-SNAPSHOT-reasoned-sa");

	private RestTestClient buildRestTestClient() {
		return RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.defaultHeader("X-API-KEY", apiKey)
				.build();
	}


	@Test
	void contextLoads() {}

	@Test
	void simpleTest() {
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
	void capabilityTest() {
		// Create REST Client
		RestTestClient client = buildRestTestClient();

		// Now you can make requests without mentioning the header again
		RestTestClient.ResponseSpec capabilityStatement = client.get()
				.uri("/api/capability")
				.exchange()
				.expectStatus().isOk();

		capabilityStatement.expectBody(CapabilityStatement.class);
	}

}
