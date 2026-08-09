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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ParentProcessor implements KnowledgeProcessor{

	private static final int PARENT_LIMIT = 10;

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

					IntIdSet allParentNids = (IntIdSet) semanticEntityVersion.fields().get(1).value();
					List<UUID> topParentIds = allParentNids.intStream()
							.mapToObj(parentNid -> EntityHandle.get(parentNid).asConcept())
							.filter(Optional::isPresent)
							.map(Optional::get)
							.sorted(Comparator.comparing(concept -> solorRequest.languageCalculatorWithCache().getDescriptionText(concept.nid()).orElse("TEXT NOT FOUND")))
							.limit(PARENT_LIMIT)
							.map(concept -> concept.publicId().asUuidList().get(0))
							.toList();

					// Need to create the Subject Concept now to add total parents count before individual parent predicates
					Resource conceptSubject = model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
							.addLiteral(SolorVocabulary.HAS_TOTAL_PARENTS, allParentNids.size());

					topParentIds.forEach(parentUUID -> {
						// Create Parent RDF resource
						Resource parent = model.createResource(SolorVocabulary.NAMESPACE + parentUUID);

						// Add Parent Resources as Predicate for Concept
						conceptSubject.addProperty(SolorVocabulary.HAS_PARENT, parent);

						// Add Parent Concept to SolorGenerationContext
						solorGenerationContext.requireTargetConcept(PublicIds.of(parentUUID));
					});
				});
	}
}
