package dev.ikm.aperture.solor.processor.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleGroup {

	private final UUID id;
	private final List<Role> roles;

	public RoleGroup(List<Role> roles) {
		this.id = UUID.randomUUID();
		this.roles = roles;
	}

	public RoleGroup() {
		this.id = UUID.randomUUID();
		this.roles = new ArrayList<>();
	}

	public UUID id() {
		return id;
	}

	public List<Role> roles() {
		return roles;
	}

	public void addRole(Role role) {
		roles.add(role);
	}
}
