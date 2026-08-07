package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.entity.Entity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConceptProcessor implements KnowledgeProcessor{

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
		int nid = Entity.nid(conceptId);
		UUID conceptUUID = conceptId.asUuidList().get(0);
		Model model = solorGenerationContext.getSolorModel();

		// Get label based on calculated Text Description
		String label = solorRequest.languageCalculatorWithCache().getDescriptionText(nid).orElse("TEXT NOT FOUND");

		// Add Concept Resource to the model
		model.createResource(SolorVocabulary.NAMESPACE + conceptUUID)
				.addProperty(RDF.type, SolorVocabulary.CONCEPT)
				.addProperty(SolorVocabulary.HAS_STATUS, processStatus(solorGenerationContext, conceptId))
				.addProperty(RDFS.label, label);
	}
}
