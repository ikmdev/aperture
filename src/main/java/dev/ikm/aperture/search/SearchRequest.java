package dev.ikm.aperture.search;

import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SearchRequest(
		@NotNull(message = "languageCoordinateId must not be null")
		UUID languageCoordinateId,
		@NotNull(message = "navigationCoordinateId must not be null")
		UUID navigationCoordinateId,
		@NotNull(message = "stampCoordinateId must not be null")
		UUID stampCoordinateId,
		List<Long> sctIds,
		List<Integer> rxnormIds,
		List<String> loincIds,
		List<UUID> ikeIds) {

	public SearchRequest {
		sctIds = Objects.requireNonNullElse(sctIds, Collections.emptyList());
		rxnormIds = Objects.requireNonNullElse(rxnormIds, Collections.emptyList());
		loincIds = Objects.requireNonNullElse(loincIds, Collections.emptyList());
		ikeIds = Objects.requireNonNullElse(ikeIds, Collections.emptyList());
	}
}
