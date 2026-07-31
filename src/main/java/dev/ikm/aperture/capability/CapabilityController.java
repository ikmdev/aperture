package dev.ikm.aperture.capability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CapabilityController {

	private final CapabilityService capabilityService;

	@Autowired
	public CapabilityController(CapabilityService capabilityService) {
		this.capabilityService = capabilityService;
	}

	@GetMapping("/capability")
	public CapabilityStatement getAllCoordinates() {
		return new CapabilityStatement(
				capabilityService.getSupportedLanguages(),
				capabilityService.getSupportedNavigations(),
				capabilityService.getSupportedStamps()
		);
	}
}
