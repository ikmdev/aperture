package dev.ikm.aperture.capability;

import dev.ikm.tinkar.coordinate.Coordinates;
import dev.ikm.tinkar.coordinate.navigation.NavigationCoordinateRecord;

import java.util.List;
import java.util.UUID;

public enum NavigationCoordinate {

	INFERRED("Inferred Navigation", List.of(UUID.fromString("10f727e4-adac-4a94-80f5-00614692aa46")), Coordinates.Navigation.inferred().toNavigationCoordinateRecord()),
	STATED("Stated Navigation", List.of(UUID.fromString("2ea11ae5-d715-48aa-839c-84e27fa5394c")), Coordinates.Navigation.inferred().toNavigationCoordinateRecord());

	private final String displayName;
	private final List<UUID> uuids;
	private final NavigationCoordinateRecord record;

	NavigationCoordinate(String displayName, List<UUID> uuids, NavigationCoordinateRecord record) {
		this.displayName = displayName;
		this.uuids = uuids;
		this.record = record;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public List<UUID> getUuids() {
		return this.uuids;
	}

	public NavigationCoordinateRecord getRecord() {
		return this.record;
	}

	public static NavigationCoordinateRecord toRecord(UUID uuid) {
		for (NavigationCoordinate coordinate : NavigationCoordinate.values()) {
			if (coordinate.uuids.contains(uuid)) {
				return coordinate.getRecord();
			}
		}
		return INFERRED.getRecord();
	}


	public static NavigationCoordinate getCoordinate(UUID uuid) {
		for (NavigationCoordinate coordinate : NavigationCoordinate.values()) {
			if (coordinate.uuids.contains(uuid)) {
				return coordinate;
			}
		}
		return INFERRED;
	}
}
