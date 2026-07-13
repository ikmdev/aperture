package dev.ikm.aperture.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

	@Value("${app.api.key}")
	private String expectedApiKey;

	public boolean validateApiKey(String apiKey) {
		return expectedApiKey.equals(apiKey);
	}
}
