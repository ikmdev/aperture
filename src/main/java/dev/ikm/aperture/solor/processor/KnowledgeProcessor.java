package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityVersion;
import dev.ikm.tinkar.terms.State;
import org.apache.jena.rdf.model.Resource;

import java.util.UUID;

public interface KnowledgeProcessor {

	/***
	 * Process the knowledge for a given concept and add it to the provided RDF solorGenerationContext.
	 * @param solorGenerationContext The context in which all modifications can be made to the RDF graph with traceability
	 * @param conceptId The concept identifier for which knowledge is being processed.
	 */
	void process(SolorGenerationContext solorGenerationContext, PublicId conceptId);

	default Resource processStatus(SolorGenerationContext solorGenerationContext, PublicId componentId) {
		int nid =  Entity.nid(componentId);
		Latest<EntityVersion> latest = solorGenerationContext.getSolorRequest().stampCalculatorWithCache().latest(nid);
		State state = Entity.getStamp(latest.get().stampNid()).state();
		UUID statusId = state.publicId().asUuidList().get(0);
		solorGenerationContext.requireConcept(state.publicId());
		return solorGenerationContext.getSolorModel().createResource(SolorVocabulary.NAMESPACE + statusId);
	}
}
