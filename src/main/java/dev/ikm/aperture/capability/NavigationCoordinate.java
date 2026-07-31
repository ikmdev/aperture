package dev.ikm.aperture.capability;

import dev.ikm.tinkar.coordinate.Coordinates;
import dev.ikm.tinkar.coordinate.navigation.NavigationCoordinateRecord;

import java.util.List;
import java.util.UUID;

public enum NavigationCoordinate {

	INFERRED("Inferred Navigation", List.of(UUID.fromString("10f727e4-adac-4a94-80f5-00614692aa46")), Coordinates.Navigation.inferred().toNavigationCoordinateRecord()),
	STATED("Stated Navigation", List.of(UUID.fromString("2ea11ae5-d715-48aa-839c-84e27fa5394c")), Coordinates.Navigation.inferred().toNavigationCoordinateRecord());

	private final String name;
	private final List<UUID> uuids;
	private final NavigationCoordinateRecord record;

	NavigationCoordinate(String name, List<UUID> uuids, NavigationCoordinateRecord record) {
		this.name = name;
		this.uuids = uuids;
		this.record = record;
	}

	public String getName() {
		return this.name;
	}

	public List<UUID> getUuids() {
		return this.uuids;
	}

	public NavigationCoordinateRecord getRecord() {
		return this.record;
	}

	public static NavigationCoordinateRecord toRecord(UUID uuid) {
		for (NavigationCoordinate coord : NavigationCoordinate.values()) {
			if (coord.uuids.contains(uuid)) {
				return coord.getRecord();
			}
		}
		return INFERRED.getRecord();
	}
}
