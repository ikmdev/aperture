package dev.ikm.aperture.search;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SearchRequest(
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
