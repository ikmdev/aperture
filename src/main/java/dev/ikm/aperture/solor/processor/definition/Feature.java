package dev.ikm.aperture.solor.processor.definition;

import dev.ikm.tinkar.terms.EntityProxy.Concept;

public class Feature {

	private final Concept featureType;
	private final Concept concreteValueOperator;
	private final String literalValue;

	public Feature(Concept featureType, Concept concreteValueOperator, String literalValue) {
		this.featureType = featureType;
		this.concreteValueOperator = concreteValueOperator;
		this.literalValue = literalValue;
	}

	public Concept getFeatureType() {
		return featureType;
	}

	public Concept getConcreteValueOperator() {
		return concreteValueOperator;
	}

	public String getLiteralValue() {
		return literalValue;
	}
}
