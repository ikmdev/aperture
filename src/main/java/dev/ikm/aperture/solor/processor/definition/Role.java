package dev.ikm.aperture.solor.processor.definition;

import dev.ikm.tinkar.terms.EntityProxy.Concept;


public class Role {

	private final Concept predicate;
	private Reference reference;

	public Role(Concept predicate, Reference reference) {
		this.predicate = predicate;
		this.reference = reference;
	}

	public Role(Concept predicate) {
		this.predicate = predicate;
	}

	public Concept predicate() {
		return predicate;
	}

	public Reference reference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
}
