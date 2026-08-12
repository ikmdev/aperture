package dev.ikm.aperture.solor.processor.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleGroup {

	private final UUID id;
	private final List<Role> roles;
	private final List<Feature> features;

	public RoleGroup(List<Role> roles) {
		this.id = UUID.randomUUID();
		this.roles = roles;
		this.features = new ArrayList<>();
	}

	public RoleGroup() {
		this.id = UUID.randomUUID();
		this.roles = new ArrayList<>();
		this.features = new ArrayList<>();
	}

	public UUID id() {
		return id;
	}

	public List<Role> roles() {
		return roles;
	}

	public List<Feature> features() {
		return features;
	}

	public void addRole(Role role) {
		roles.add(role);
	}

	public void addFeature(Feature feature) {
		features.add(feature);
	}
}
