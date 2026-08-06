package dev.ikm.aperture.solor.processor;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class SolorVocabulary {

	public static final String NAMESPACE = "https://www.ikm.dev/solor/";

	public static final String PREFIX = "solor";

	// --- Core Classes (Resources) ---
	public static final Resource CONCEPT = ResourceFactory.createResource(NAMESPACE + "Concept");

	// --- Core Properties (Edges) ---
	public static final Property HAS_CONCEPT_ID = ResourceFactory.createProperty(NAMESPACE, "has_concept_id");

	public static final Property HAS_FULLY_QUALIFIED_NAME = ResourceFactory.createProperty(NAMESPACE, "has_fully_qualified_name");
	public static final Property HAS_SYNONYM = ResourceFactory.createProperty(NAMESPACE, "has_synonym");
	public static final Property HAS_DEFINITION = ResourceFactory.createProperty(NAMESPACE, "has_definition");

	public static final Property HAS_IDENTIFIER = ResourceFactory.createProperty(NAMESPACE, "has_identifier");
	public static final Property HAS_IDENTIFIER_SYSTEM = ResourceFactory.createProperty(NAMESPACE, "has_identifier_system");

	public static final Property HAS_PARENT = ResourceFactory.createProperty(NAMESPACE, "has_parent");
	public static final Property HAS_ANCESTOR = ResourceFactory.createProperty(NAMESPACE, "has_ancestor");

	public static final Property HAS_CHILDREN = ResourceFactory.createProperty(NAMESPACE, "has_children");

	public static final Property HAS_MEMBERSHIP = ResourceFactory.createProperty(NAMESPACE, "has_membership");

	// Private constructor prevents instantiation
	private SolorVocabulary() {}

}
