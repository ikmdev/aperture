package dev.ikm.aperture.solor;

import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.coordinate.language.LanguageCoordinateRecord;
import dev.ikm.tinkar.coordinate.language.calculator.LanguageCalculatorWithCache;
import dev.ikm.tinkar.coordinate.navigation.NavigationCoordinateRecord;
import dev.ikm.tinkar.coordinate.navigation.calculator.NavigationCalculatorWithCache;
import dev.ikm.tinkar.coordinate.stamp.StampCoordinateRecord;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculatorWithCache;

import java.util.List;

public record SolorRequest(
		LanguageCalculatorWithCache languageCalculatorWithCache,
		NavigationCalculatorWithCache navigationCalculatorWithCache,
		StampCalculatorWithCache stampCalculatorWithCache,
		List<PublicId> conceptIds) {
}
