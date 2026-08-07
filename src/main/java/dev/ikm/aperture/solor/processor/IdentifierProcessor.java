package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.terms.ConceptFacade;
import dev.ikm.tinkar.terms.TinkarTermV2;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentifierProcessor implements KnowledgeProcessor {

	Logger LOG = LoggerFactory.getLogger(IdentifierProcessor.class);

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
		int nid = Entity.nid(conceptId);
		UUID conceptUUID = conceptId.asUuidList().get(0);
		Model model = solorGenerationContext.getSolorModel();

		solorRequest.stampCalculatorWithCache().forEachSemanticVersionForComponentOfPattern(
				nid,
				TinkarTermV2.IDENTIFIER_PATTERN.nid(),
				(semanticEntityVersion, _, _) -> {
					// Get Identifier System Concept from Semantic
					ConceptFacade identifierSystemFacade = semanticEntityVersion.fieldAsConceptFacade(0);
					UUID identifierSystemId = identifierSystemFacade.publicId().asUuidList().get(0);
					Resource indentiferSystemResource = model.createResource(SolorVocabulary.NAMESPACE + identifierSystemId);

					// Get Identifier Value from Semantic
					String identifierValue = semanticEntityVersion.fieldAsString(1);

					// Create an identifier node to pair the two predicates together
					Resource identifierNode = model.createResource()
							.addProperty(SolorVocabulary.HAS_IDENTIFIER_VALUE, identifierValue)
							.addProperty(SolorVocabulary.HAS_IDENTIFIER_SYSTEM, indentiferSystemResource)
							.addProperty(SolorVocabulary.HAS_STATUS, processStatus(solorGenerationContext, semanticEntityVersion.publicId()));

					// Link the identifier node to the original concept resource
					model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
							.addProperty(SolorVocabulary.HAS_IDENTIFIER, identifierNode);

					// Add Identifier Concept to SolorGenerationContext
					solorGenerationContext.requireTargetConcept(identifierSystemFacade.publicId());
				});
	}
}
