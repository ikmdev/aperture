package dev.ikm.aperture.config;

import dev.ikm.aperture.security.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

	// Inject your dedicated service into the Filter
	private final SecurityService securityService;

	public ApiKeyFilter(SecurityService securityService) {
		this.securityService = securityService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

		String requestKey = request.getHeader("X-API-KEY");

		// The Filter delegates the complex logic to the Service
		if (!securityService.validateApiKey(requestKey)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		try {
			filterChain.doFilter(request, response);
		} catch (IOException | ServletException e) {
			throw new RuntimeException(e);
		}
	}
}
