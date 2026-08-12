package dev.ikm.aperture.solor;

import dev.ikm.aperture.solor.processor.ConceptProcessor;
import dev.ikm.aperture.solor.processor.IdentifierProcessor;
import dev.ikm.aperture.solor.processor.KnowledgeProcessor;
import dev.ikm.aperture.solor.processor.SolorVocabulary;
import dev.ikm.tinkar.common.id.PublicId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

@Service
public class SolorService {

	private static final Logger LOG = LoggerFactory.getLogger(SolorService.class);

	private final List<KnowledgeProcessor> knowledgeProcessors;
	private final Model baseOntModelTemplate;

	@Autowired
	public SolorService(List<KnowledgeProcessor> knowledgeProcessors) {
		this.knowledgeProcessors = knowledgeProcessors;
		this.baseOntModelTemplate = ModelFactory.createDefaultModel();
		this.baseOntModelTemplate.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.baseOntModelTemplate.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		this.baseOntModelTemplate.setNsPrefix("solor", SolorVocabulary.NAMESPACE);
	}

	public String constructSolorGraph(SolorRequest solorRequest) {
		// Per request build a default RDF model
		Model solorGraph = ModelFactory.createDefaultModel();
		SolorGenerationContext solorGenerationContext = new SolorGenerationContext(solorRequest, solorGraph);

		// Take into account not having any concept identifiers passed in
		if (solorRequest.conceptIds().isEmpty()) {
			// Handle empty conceptIds case by generating an empty Solor graph
			generateEmptySolorGraph(solorGenerationContext);
		} else {
			// Handle creating a solor sub-graph based on requests concept ids
			generateSolorGraph(solorGenerationContext, solorRequest.conceptIds());
		}

		// Create writer to return Turtle format of solor graph (RDF Model)
		StringWriter writer = new StringWriter();
		solorGraph.write(writer, "TURTLE");

		return writer.toString();
	}

	private void generateEmptySolorGraph(SolorGenerationContext solorGenerationContext) {
		LOG.info("Generating empty solor graph");
		solorGenerationContext.getSolorModel().add(baseOntModelTemplate);
	}

	private void generateSolorGraph(SolorGenerationContext solorGenerationContext, Set<PublicId> conceptIds) {
		LOG.info("Generating solor graph from {} concepts", conceptIds.size());

		// Add the base ontology model template to the Solor model
		solorGenerationContext.getSolorModel().add(baseOntModelTemplate);

		for (PublicId conceptId : conceptIds) {
			// Loop through all the supported Knowledge Processors
			for (KnowledgeProcessor knowledgeProcessor : knowledgeProcessors) {
				knowledgeProcessor.process(solorGenerationContext, conceptId);
			}
		}

		// After the main processing phase run processing to build out dependent concept and predicates
		KnowledgeProcessor conceptProcessor = new ConceptProcessor();
		KnowledgeProcessor identifierProcessor = new IdentifierProcessor();
		Set<PublicId> pendingConceptBatch;
		while (!(pendingConceptBatch = solorGenerationContext.getAndClearPendingConceptIds()).isEmpty()) {
			for (PublicId conceptId : pendingConceptBatch) {
				conceptProcessor.process(solorGenerationContext, conceptId);
				identifierProcessor.process(solorGenerationContext, conceptId);
			}
		}

		// After the main processing phase run processing to build out dependent predicates
		Set<PublicId> pendingPredicateBatch;
		while (!(pendingPredicateBatch = solorGenerationContext.getAndClearPendingPredicateIds()).isEmpty()) {
			for (PublicId predicateId : pendingPredicateBatch) {
				// Process the concept itself
				// Process the rdfs:type Predicate
				solorGenerationContext.getSolorModel()
						.createResource(SolorVocabulary.NAMESPACE + predicateId.asUuidList().get(0))
						.addProperty(RDF.type, RDF.Property);
			}
		}
	}

}
