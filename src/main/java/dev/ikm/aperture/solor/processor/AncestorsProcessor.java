package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.IntIdSet;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityHandle;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AncestorsProcessor implements KnowledgeProcessor {

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
		int nid = Entity.nid(conceptId);
		UUID conceptUUID = conceptId.asUuidList().get(0);
		Model model = solorGenerationContext.getSolorModel();

		IntIdSet allAncestorsNids = solorRequest.navigationCalculatorWithCache().ancestorsOf(nid);
		List<UUID> topAncestorIds = allAncestorsNids.intStream()
				.mapToObj(ancestorNid -> EntityHandle.get(ancestorNid).asConcept())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.sorted(Comparator.comparing(concept -> solorRequest.languageCalculatorWithCache().getDescriptionText(concept.nid()).orElse("TEXT NOT FOUND")))
				.limit(solorRequest.ancestorsDepth())
				.map(concept -> concept.publicId().asUuidList().get(0))
				.toList();

		// Need to create the Subject Concept now to add total parents count before individual parent predicates
		Resource conceptSubject = model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
				.addLiteral(SolorVocabulary.HAS_TOTAL_ANCESTORS, allAncestorsNids.size());

		topAncestorIds.forEach(parentUUID -> {
			// Create Parent RDF resource
			Resource parent = model.createResource(SolorVocabulary.NAMESPACE + parentUUID);

			// Add Parent Resources as Predicate for Concept
			conceptSubject.addProperty(SolorVocabulary.HAS_ANCESTOR, parent);

			// Add Parent Concept to SolorGenerationContext
			solorGenerationContext.requireConcept(PublicIds.of(parentUUID));
		});
	}

}
