package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.terms.ConceptFacade;
import dev.ikm.tinkar.terms.TinkarTermV2;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FullyQualifiedNameProcessor implements KnowledgeProcessor {

	/*
	≤s:Active t:2017-07-30 20:00:00 a:SNOMED CT Author m:SNOMED CT core module p:Development path <snid: -2147477033>
Field 1: ‹Language for description: English language› Concept
Field 2: ‹Text: Deep venous thrombosis› String
Field 3: ‹Description case significance: Case insensitive› Concept
Field 4: ‹Description type: Regular name description type› Concept≥
	 */

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
		int nid = Entity.nid(conceptId);
		UUID conceptUUID = conceptId.asUuidList().get(0);
		Model model = solorGenerationContext.getSolorModel();

		solorRequest.stampCalculatorWithCache().forEachSemanticVersionForComponentOfPattern(
				nid,
				TinkarTermV2.DESCRIPTION_PATTERN.nid(),
				(semanticEntityVersion, _, _) -> {
					if (semanticEntityVersion.fieldAsConceptFacade(3).nid() == TinkarTermV2.FULLY_QUALIFIED_NAME_DESCRIPTION_TYPE.nid()) {
						// Get Fully Qualified Name from Semantic
						String fullyQualifiedName = semanticEntityVersion.fieldAsString(1);
						ConceptFacade caseSensitivityFacade = semanticEntityVersion.fieldAsConceptFacade(2);
						UUID caseSensitivityId = caseSensitivityFacade.publicId().asUuidList().get(0);

						// Create Case Sensitivity Concept
						Resource caseSensitivity = model.createResource(SolorVocabulary.NAMESPACE + caseSensitivityId);

						// Create Description Node to pair both text and case sensitivity together
						Resource descriptionNode = model.createResource()
								.addProperty(SolorVocabulary.HAS_FULLY_QUALIFIED_NAME, fullyQualifiedName, solorRequest.languageCoordinate().getISOCode())
								.addProperty(SolorVocabulary.HAS_CASE_SENSITIVITY, caseSensitivity)
								.addProperty(SolorVocabulary.HAS_STATUS, processStatus(solorGenerationContext, semanticEntityVersion.publicId()));

						// Add Fully Qualified Name to Concept Resource in the model
						model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
								.addProperty(SolorVocabulary.HAS_DESCRIPTION, descriptionNode);

						// Add Identifier Concept to SolorGenerationContext
						solorGenerationContext.requireConcept(caseSensitivityFacade.publicId());
					}
				});
	}

}
