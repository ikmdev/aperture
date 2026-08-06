package dev.ikm.aperture.solor;

import dev.ikm.aperture.capability.LanguageCoordinate;
import dev.ikm.aperture.capability.NavigationCoordinate;
import dev.ikm.aperture.capability.StampCoordinate;
import dev.ikm.aperture.search.SearchRequest;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.common.util.uuid.UuidT5Generator;
import dev.ikm.tinkar.coordinate.language.LanguageCoordinateRecord;
import dev.ikm.tinkar.coordinate.language.calculator.LanguageCalculatorWithCache;
import dev.ikm.tinkar.coordinate.navigation.NavigationCoordinateRecord;
import dev.ikm.tinkar.coordinate.navigation.calculator.NavigationCalculatorWithCache;
import dev.ikm.tinkar.coordinate.stamp.StampCoordinateRecord;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculatorWithCache;
import org.eclipse.collections.api.factory.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class SolorMapper {

	private static final Logger LOG = LoggerFactory.getLogger(SolorMapper.class);

	private static final UUID SNOMED_CT_NAMESPACE = UUID.fromString("3094dbd1-60cf-44a6-92e3-0bb32ca4d3de");
	private static final UUID LOINC_NAMESPACE = UUID.fromString("7b880c4c-8e54-5625-863c-c8504fa78243");
	private static final UUID RXNORM_NAMESPACE = UUID.fromString("3094dbd1-60cf-44a6-92e3-0bb32ca4d3de");

	// Private constructor prevents instantiation
	private SolorMapper() {
		LOG.info("SolorMapper()");
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**     * Maps a web-layer SearchRequest to a domain-layer SolorRequest.     */
	public static SolorRequest toSolorRequest(SearchRequest searchRequest) {
		List<PublicId> conceptIds = new ArrayList<>();

		// Snomed CT Identifier Conversion
		searchRequest.sctIds().forEach(id -> {
			UUID uuid = UuidT5Generator.get(SNOMED_CT_NAMESPACE, id.toString());
			PublicId publicId = PublicIds.of(uuid);
			conceptIds.add(publicId);
		});

		// LOINC Identifier Conversion
		searchRequest.loincIds().forEach(id -> {
			UUID uuid = UuidT5Generator.get(LOINC_NAMESPACE, id);
			PublicId publicId = PublicIds.of(uuid);
			conceptIds.add(publicId);
		});

		// RxNorm Identifier Conversion
		searchRequest.rxnormIds().forEach(id -> {
			UUID uuid = UuidT5Generator.get(RXNORM_NAMESPACE, id.toString());
			PublicId publicId = PublicIds.of(uuid);
			conceptIds.add(publicId);
		});

		// Create Stamp Coordinate Record and Calculator
		StampCoordinateRecord stampCoordinateRecord = StampCoordinate.toRecord(searchRequest.stampCoordinateId());
		StampCalculatorWithCache stampCalculatorWithCache = StampCalculatorWithCache.getCalculator(stampCoordinateRecord);

		// Create Language Coordinate Record and Calculator
		LanguageCoordinateRecord languageCoordinateRecord = LanguageCoordinate.toRecord(searchRequest.languageCoordinateId());
		LanguageCalculatorWithCache languageCalculatorWithCache = LanguageCalculatorWithCache.getCalculator(stampCoordinateRecord, Lists.immutable.of(languageCoordinateRecord));

		// Create Navigation Coordinate Record and Calculator
		NavigationCoordinateRecord navigationCoordinateRecord = NavigationCoordinate.toRecord(searchRequest.navigationCoordinateId());
		NavigationCalculatorWithCache navigationCalculatorWithCache = NavigationCalculatorWithCache.getCalculator(stampCoordinateRecord, Lists.immutable.of(languageCoordinateRecord), navigationCoordinateRecord);

		return new SolorRequest(languageCalculatorWithCache, navigationCalculatorWithCache, stampCalculatorWithCache, conceptIds);
	}
}
