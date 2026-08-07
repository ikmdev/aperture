package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.IntIdSet;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.Field;
import dev.ikm.tinkar.terms.ConceptFacade;
import dev.ikm.tinkar.terms.TinkarTermV2;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class ChildrenProcessor implements KnowledgeProcessor{

	private static final int CHILDREN_LIMIT = 20;

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
//		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
//		int nid = Entity.nid(conceptId);
//		UUID conceptUUID = conceptId.asUuidList().get(0);
//		Model model = solorGenerationContext.getSolorModel();
//		List<ConceptFacade> children = new ArrayList<>();
//
//		solorRequest.stampCalculatorWithCache().forEachSemanticVersionForComponentOfPattern(
//				nid,
//				TinkarTermV2.INFERRED_NAVIGATION_PATTERN.nid(),
//				(semanticEntityVersion, _, _) -> {
//
//					IntIdSet childrenIds = (IntIdSet) semanticEntityVersion.fields().get(0).value();
//					List<ConceptFacade> childrenList = childrenIds.intStream()
//							.sorted(Comparator.comparing(id -> solorRequest.languageCalculatorWithCache().getDescriptionText(id).orElse("")))
//							.limit(CHILDREN_LIMIT)
//							.mapToObj(solorRequest::conceptForNid)
//							.toList();
//					children.addAll(childrenList);
//
//					if (semanticEntityVersion.fieldAsConceptFacade(3).nid() == TinkarTermV2.REGULAR_NAME_DESCRIPTION_TYPE.nid()) {
//						// Get Synonym from Semantic
//						String synonym = semanticEntityVersion.fieldAsString(1);
//						ConceptFacade caseSensitivityFacade = semanticEntityVersion.fieldAsConceptFacade(2);
//						UUID caseSensitivityId = caseSensitivityFacade.publicId().asUuidList().get(0);
//
//						// Create Case Sensitivity Concept
//						Resource caseSensitivity = model.createResource(SolorVocabulary.NAMESPACE + caseSensitivityId);
//
//						// Create Description Node to pair both text and case sensitivity together
//						Resource descriptionNode = model.createResource()
//								.addProperty(SolorVocabulary.HAS_SYNONYM, synonym, solorRequest.languageCoordinate().getISOCode())
//								.addProperty(SolorVocabulary.HAS_CASE_SENSITIVITY, caseSensitivity)
//								.addProperty(SolorVocabulary.HAS_STATUS, processStatus(solorGenerationContext, semanticEntityVersion.publicId()));
//
//						// Add Synonym to Concept Resource in the model
//						model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
//								.addProperty(SolorVocabulary.HAS_DESCRIPTION, descriptionNode);
//
//						// Add Identifier Concept to SolorGenerationContext
//						solorGenerationContext.requireTargetConcept(caseSensitivityFacade.publicId());
//					}
//				});
	}
}
