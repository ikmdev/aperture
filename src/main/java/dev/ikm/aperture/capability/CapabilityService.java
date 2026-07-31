package dev.ikm.aperture.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CapabilityService {

	private final static Logger LOG =  LoggerFactory.getLogger(CapabilityService.class);

	public List<Coordinate> getSupportedLanguages() {
		return Arrays.stream(LanguageCoordinate.values())
				.map(languageCoordinate -> new Coordinate(languageCoordinate.name(), languageCoordinate.getUuids()))
				.toList();
	}

	public List<Coordinate> getSupportedNavigations() {
		return Arrays.stream(NavigationCoordinate.values())
				.map(navigationCoordinate -> new Coordinate(navigationCoordinate.name(), navigationCoordinate.getUuids()))
				.toList();
	}

	public List<Coordinate> getSupportedStamps() {
		return Arrays.stream(StampCoordinate.values())
				.map(stampCoordinate -> new Coordinate(stampCoordinate.name(), stampCoordinate.getUuids()))
				.toList();
	}


}
