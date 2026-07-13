package dev.ikm.aperture.solor;

import java.util.List;
import java.util.UUID;

public record SolorRequest(
		List<UUID> conceptUUIDs) {
}
