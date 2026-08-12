package dev.ikm.aperture.solor.processor.definition;

import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.TinkarTermV2;

public enum Type {

	DEFINITION_ROOT(TinkarTermV2.DEFINITION_ROOT),
	AND(TinkarTermV2.AND),
	OR(TinkarTermV2.OR),
	NECESSARY_SET(TinkarTermV2.NECESSARY_SET),
	SUFFICIENT_SET(TinkarTermV2.SUFFICIENT_SET),
	PROPERTY_SET(TinkarTermV2.PROPERTY_SET),
	INTERVAL_PROPERTY_SET(TinkarTermV2.INTERVAL_PROPERTY_SET),
	DATA_PROPERTY_SET(TinkarTermV2.DATA_PROPERTY_SET),
	ROLE_GROUP(TinkarTermV2.ROLE_GROUP),
	ROLE(TinkarTermV2.ROLE),
	INTERVAL_ROLE(TinkarTermV2.INTERVAL_ROLE),
	CONCRETE_ROLE(TinkarTermV2.CONCRETE_VALUE_OPERATOR),
	FEATURE(TinkarTermV2.FEATURE),
	REFERENCE(TinkarTermV2.CONCEPT_REFERENCE);

	private EntityProxy.Concept tinkarTerm;

	Type(EntityProxy.Concept tinkarTerm) {
		this.tinkarTerm = tinkarTerm;
	}

	public EntityProxy.Concept getTinkarTerm() {
		return tinkarTerm;
	}

	public int nid() {
		return tinkarTerm.nid();
	}

	public static boolean isType(int nid) {
		for (Type type : values()) {
			if (type.nid() == nid) {
				return true;
			}
		}
		return false;
	}

	public static Type fromNid(int nid) {
		for (Type type : values()) {
			if (type.nid() == nid) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown nid: " + nid);
	}
}
