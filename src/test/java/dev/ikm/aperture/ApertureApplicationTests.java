package dev.ikm.aperture;

import dev.ikm.aperture.search.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;


@SpringBootTest(classes = ApertureApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"app.api.key=c4bba3d8-cd4c-481f-9151-4bc68903bb98"})
class ApertureApplicationTests {

	@LocalServerPort
	private int port;


	@Test
	void contextLoads() {
	}

	@Test
	void simpleTest() {
		SearchRequest searchRequest = new SearchRequest(List.of(), List.of(), List.of(), List.of());

		// Set it once during the build step
		RestTestClient client = RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.defaultHeader("X-API-KEY", "c4bba3d8-cd4c-481f-9151-4bc68903bb98")
				.build();

		// Now you can make requests without mentioning the header again
		client.post()
				.uri("/api/search")
				.contentType(MediaType.APPLICATION_JSON)
				.body(searchRequest)
				.exchange()
				.expectStatus().isOk();

	}

}
