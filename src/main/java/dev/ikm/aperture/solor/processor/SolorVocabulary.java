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

	public static final Property HAS_STATUS = ResourceFactory.createProperty(NAMESPACE, "has_status");

	public static final Property HAS_DESCRIPTION = ResourceFactory.createProperty(NAMESPACE, "has_description");
	public static final Property HAS_FULLY_QUALIFIED_NAME = ResourceFactory.createProperty(NAMESPACE, "has_fully_qualified_name");
	public static final Property HAS_SYNONYM = ResourceFactory.createProperty(NAMESPACE, "has_synonym");
	public static final Property HAS_DEFINITION = ResourceFactory.createProperty(NAMESPACE, "has_definition");
	public static final Property HAS_CASE_SENSITIVITY = ResourceFactory.createProperty(NAMESPACE, "has_case_sensitivity");

	public static final Property HAS_IDENTIFIER = ResourceFactory.createProperty(NAMESPACE, "has_identifier");
	public static final Property HAS_IDENTIFIER_VALUE = ResourceFactory.createProperty(NAMESPACE, "has_identifier_value");
	public static final Property HAS_IDENTIFIER_SYSTEM = ResourceFactory.createProperty(NAMESPACE, "has_identifier_system");

	public static final Property IS_A = ResourceFactory.createProperty(NAMESPACE, "is_a");
	public static final Property HAS_TOTAL_CHILDREN = ResourceFactory.createProperty(NAMESPACE, "has_total_children");
	public static final Property HAS_CHILD = ResourceFactory.createProperty(NAMESPACE, "has_child");

	// Private constructor prevents instantiation
	private SolorVocabulary() {}

}
