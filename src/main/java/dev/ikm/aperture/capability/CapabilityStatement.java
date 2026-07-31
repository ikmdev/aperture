package dev.ikm.aperture.capability;

import java.util.List;

public record CapabilityStatement(
		List<Coordinate> languageCoordinates,
		List<Coordinate> navigationCoordinates,
		List<Coordinate> stampCoordinates) {
}
