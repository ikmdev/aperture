package dev.ikm.aperture.capability;

import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.coordinate.Coordinates;
import dev.ikm.tinkar.coordinate.stamp.StampCoordinateRecord;
import dev.ikm.tinkar.entity.Entity;

import java.util.List;
import java.util.UUID;

public enum StampCoordinate {

	DEV_LATEST("Latest on the Development Path", List.of(UUID.fromString("1767ad74-0b89-4601-b293-89dc0c51917a")), Coordinates.Stamp.DevelopmentLatest()),
	DEV_LATEST_ACTIVE_ONLY("Latest Active on the Development Path", List.of(UUID.fromString("6a5091d1-d30a-4a31-bfc1-bcf09639574e")), Coordinates.Stamp.DevelopmentLatestActiveOnly()),
	DEV_LATEST_INACTIVE_ONLY("Latest Inactive on the Development Path", List.of(UUID.fromString("fecf9dea-269f-423d-8b4e-94de36458957")), Coordinates.Stamp.DevelopmentLatestInactiveOnly()),
	MASTER_LATEST("Latest on the Master Path", List.of(UUID.fromString("5bbcab20-dc56-4dcc-bdbc-5957ddf559e8")), Coordinates.Stamp.MasterLatest()),
	MASTER_LATEST_ACTIVE_ONLY("Latest Active on the Master Path", List.of(UUID.fromString("4fc5f3f3-704a-44a2-88b6-7ce1b84c7422")), Coordinates.Stamp.MasterLatestActiveOnly()),
	;

	private final String displayName;
	private final List<UUID> uuids;
	private final StampCoordinateRecord record;

	StampCoordinate(String displayName, List<UUID> uuids, StampCoordinateRecord record) {
		this.displayName = displayName;
		this.uuids = uuids;
		this.record = record;
		PublicId publicId = PublicIds.of(uuids);
		int nid = Entity.nid(publicId);
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public List<UUID> getUuids() {
		return this.uuids;
	}

	public StampCoordinateRecord getRecord() {
		return this.record;
	}

	public static StampCoordinateRecord toRecord(UUID uuid) {
		for (StampCoordinate coordinate : StampCoordinate.values()) {
			if (coordinate.uuids.contains(uuid)) {
				return coordinate.getRecord();
			}
		}
		return DEV_LATEST.getRecord();
	}
}
