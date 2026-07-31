package dev.ikm.aperture;

import dev.ikm.aperture.capability.CapabilityStatement;
import dev.ikm.aperture.search.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@SpringBootTest(classes = ApertureApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"app.api.key=c4bba3d8-cd4c-481f-9151-4bc68903bb98"})
class ApertureApplicationTests {

	@LocalServerPort
	private int port;

	Path ikeDB = Paths.get(System.getProperty("user.dir"), "target").resolve("data").resolve("solor-export-1.0.0-SNAPSHOT-reasoned-sa");

	private RestTestClient buildRestTestClient() {
		return RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.defaultHeader("X-API-KEY", "c4bba3d8-cd4c-481f-9151-4bc68903bb98")
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
