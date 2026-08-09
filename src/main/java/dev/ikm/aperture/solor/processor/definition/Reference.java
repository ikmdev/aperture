package dev.ikm.aperture.solor.processor.definition;

import dev.ikm.tinkar.terms.EntityProxy.Concept;


public class Reference {

	private final Concept concept;

	public Reference(Concept concept) {
		this.concept = concept;
	}

	public Concept concept() {
		return concept;
	}
}
