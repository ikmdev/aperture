package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.tinkar.common.id.PublicId;

public interface KnowledgeProcessor {

	/***
	 * Process the knowledge for a given concept and add it to the provided RDF solorGenerationContext.
	 * @param solorGenerationContext The context in which all modifications can be made to the RDF graph with traceability
	 * @param conceptId The concept identifier for which knowledge is being processed.
	 */
	void process(SolorGenerationContext solorGenerationContext, PublicId conceptId);
}
