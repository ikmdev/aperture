package dev.ikm.aperture.solor;

import dev.ikm.aperture.capability.LanguageCoordinate;
import dev.ikm.aperture.capability.NavigationCoordinate;
import dev.ikm.aperture.capability.StampCoordinate;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.coordinate.language.LanguageCoordinateRecord;
import dev.ikm.tinkar.coordinate.language.calculator.LanguageCalculatorWithCache;
import dev.ikm.tinkar.coordinate.navigation.NavigationCoordinateRecord;
import dev.ikm.tinkar.coordinate.navigation.calculator.NavigationCalculatorWithCache;
import dev.ikm.tinkar.coordinate.stamp.StampCoordinateRecord;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculatorWithCache;

import java.util.List;
import java.util.Set;

public record SolorRequest(
		LanguageCoordinate languageCoordinate,
		NavigationCoordinate navigationCoordinate,
		StampCoordinate stampCoordinate,
		LanguageCalculatorWithCache languageCalculatorWithCache,
		NavigationCalculatorWithCache navigationCalculatorWithCache,
		StampCalculatorWithCache stampCalculatorWithCache,
		Set<PublicId> conceptIds) {
}
