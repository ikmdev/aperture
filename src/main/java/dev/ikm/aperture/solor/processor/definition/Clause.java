package dev.ikm.aperture.solor.processor.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clause {

	private final UUID id;
	private final Type type;
	private final List<RoleGroup> roleGroups;
	private final List<Role> roles;
	private final List<Reference> references;

	public Clause(Type type, List<Object> content) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.roleGroups = new ArrayList<>();
		this.roles = new ArrayList<>();
		this.references = new ArrayList<>();
	}

	public Clause(Type type) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.roleGroups = new ArrayList<>();
		this.roles = new ArrayList<>();
		this.references = new ArrayList<>();
	}

	public UUID id() {
		return id;
	}

	public Type element() {
		return type;
	}

	public List<RoleGroup> roleGroups() {
		return roleGroups;
	}

	public List<Role> roles() {
		return roles;
	}

	public List<Reference> references() {
		return references;
	}

	public void addRoleGroup(RoleGroup roleGroup) {
		roleGroups.add(roleGroup);
	}

	public void addRole(Role role) {
		roles.add(role);
	}

	public void addReference(Reference reference) {
		references.add(reference);
	}

}
