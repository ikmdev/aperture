package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.IntIdSet;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityHandle;
import dev.ikm.tinkar.terms.TinkarTermV2;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChildrenProcessor implements KnowledgeProcessor{

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
		int nid = Entity.nid(conceptId);
		UUID conceptUUID = conceptId.asUuidList().get(0);
		Model model = solorGenerationContext.getSolorModel();

		solorRequest.stampCalculatorWithCache().forEachSemanticVersionForComponentOfPattern(
				nid,
				TinkarTermV2.INFERRED_NAVIGATION_PATTERN.nid(),
				(semanticEntityVersion, _, _) -> {

					IntIdSet allChildrenNids = (IntIdSet) semanticEntityVersion.fields().get(0).value();
					List<UUID> topChildrenIds = allChildrenNids.intStream()
							.mapToObj(childNid -> EntityHandle.get(childNid).asConcept())
							.filter(Optional::isPresent)
							.map(Optional::get)
							.sorted(Comparator.comparing(concept -> solorRequest.languageCalculatorWithCache().getDescriptionText(concept.nid()).orElse("TEXT NOT FOUND")))
							.limit(solorRequest.childrenDepth())
							.map(concept -> concept.publicId().asUuidList().get(0))
							.toList();

					// Need to create the Subject Concept now to add total children count before individual children predicates
					Resource conceptSubject = model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
									.addLiteral(SolorVocabulary.HAS_TOTAL_CHILDREN, allChildrenNids.size());

					topChildrenIds.forEach(childUUID -> {
						// Create Child RDF resource
						Resource child = model.createResource(SolorVocabulary.NAMESPACE + childUUID);

						// Add Child Resources as Predicate for Concept
						conceptSubject.addProperty(SolorVocabulary.HAS_CHILD, child);

						// Add Child Concept to SolorGenerationContext
						solorGenerationContext.requireConcept(PublicIds.of(childUUID));
					});
				});
	}
}
