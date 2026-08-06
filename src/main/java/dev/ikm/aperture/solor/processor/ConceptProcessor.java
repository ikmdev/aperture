package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.entity.Entity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Component;

@Component
public class ConceptProcessor implements KnowledgeProcessor{

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		int nid = Entity.nid(conceptId);
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();

		String label = solorRequest.languageCalculatorWithCache().getDescriptionText(nid).orElse("TEXT NOT FOUND");

		Model model = solorGenerationContext.getSolorModel();
		model.createResource(SolorVocabulary.NAMESPACE + conceptId.asUuidList().get(0))
				.addProperty(RDF.type, SolorVocabulary.CONCEPT)
				.addProperty(RDFS.label, label);
	}
}
