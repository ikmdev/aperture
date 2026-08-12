package dev.ikm.aperture.solor;

import dev.ikm.aperture.solor.processor.ConceptProcessor;
import dev.ikm.aperture.solor.processor.IdentifierProcessor;
import dev.ikm.aperture.solor.processor.KnowledgeProcessor;
import dev.ikm.aperture.solor.processor.SolorVocabulary;
import dev.ikm.tinkar.common.id.PublicId;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

@Component
public class SolorPipeline {

	private final static Logger LOG = LoggerFactory.getLogger(SolorPipeline.class);

	private final List<KnowledgeProcessor> knowledgeProcessors;
	private final Model baseOntModelTemplate;


	@Autowired
	public SolorPipeline(List<KnowledgeProcessor> knowledgeProcessors) {
		this.knowledgeProcessors = knowledgeProcessors;
		this.baseOntModelTemplate = ModelFactory.createDefaultModel();
		this.baseOntModelTemplate.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.baseOntModelTemplate.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		this.baseOntModelTemplate.setNsPrefix("solor", SolorVocabulary.NAMESPACE);
	}

	public String execute(SolorRequest solorRequest) {
		// Initialize Solor Generation Context
		Model solorGraph = ModelFactory.createDefaultModel();
		SolorGenerationContext context = new SolorGenerationContext(solorRequest, solorGraph);

		// Execute Solor Pipeline
		if (solorRequest.conceptIds().isEmpty()) {
			buildEmptyRDFGraph(context);
		} else {
			// 1. Get raw Tinkar database model
			buildRawRDFGraph(context);

			// 2. Make it GraphRAG friendly
			transformToGraphRagModel(context);

			// 3. Clean and refine the output
			filterGraphRagModel(context);
		}

		return convertSolorGraphToTurtleSyntax(context.getSolorModel());
	}

	private void buildEmptyRDFGraph(SolorGenerationContext context) {
		LOG.info("Build empty solor graph");
		context.getSolorModel().add(baseOntModelTemplate);
	}

	private void buildRawRDFGraph(SolorGenerationContext context) {
		LOG.info("Build raw solor graph from {} concepts", context.getSolorRequest().conceptIds().size());

		// Add the base ontology model template to the Solor model
		context.getSolorModel().add(baseOntModelTemplate);

		for (PublicId conceptId : context.getSolorRequest().conceptIds()) {
			// Loop through all the supported Knowledge Processors
			for (KnowledgeProcessor knowledgeProcessor : knowledgeProcessors) {
				knowledgeProcessor.process(context, conceptId);
			}
		}

		// After the main processing phase, run processing to build out dependent concept and predicates
		KnowledgeProcessor conceptProcessor = new ConceptProcessor();
		KnowledgeProcessor identifierProcessor = new IdentifierProcessor();
		Set<PublicId> pendingConceptBatch;
		while (!(pendingConceptBatch = context.getAndClearPendingConceptIds()).isEmpty()) {
			for (PublicId conceptId : pendingConceptBatch) {
				conceptProcessor.process(context, conceptId);
				identifierProcessor.process(context, conceptId);
			}
		}

		// After the main processing phase, run processing to build out dependent predicates
		Set<PublicId> pendingPredicateBatch;
		while (!(pendingPredicateBatch = context.getAndClearPendingPredicateIds()).isEmpty()) {
			for (PublicId predicateId : pendingPredicateBatch) {
				// Process the concept itself
				// Process the rdfs:type Predicate
				context.getSolorModel()
						.createResource(SolorVocabulary.NAMESPACE + predicateId.asUuidList().get(0))
						.addProperty(RDF.type, RDF.Property);
			}
		}
	}

	private void transformToGraphRagModel(SolorGenerationContext context) {
		LOG.info("Transform to graphRAG model");
		String transformQuery = """
				PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				 PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
				 PREFIX solor: <https://www.ikm.dev/solor/>
				
				 CONSTRUCT {
				     ?concept rdfs:label ?conceptLabel .
				     ?concept solor:has_parent ?parentLabel .
				     ?concept solor:has_child ?childLabel .
				     ?concept solor:has_synonym ?synonym .
				     ?concept solor:has_fully_qualified_name ?fqn .
				     ?concept solor:has_identifier ?formattedIdentifier .
				     ?concept ?readablePredicate ?objectLabel .
				 }
				 WHERE {
				     ?concept rdf:type solor:Concept .
				     ?concept rdfs:label ?conceptLabel .
				
				     {
				         # Flatten Descriptions
				         ?concept solor:has_description ?descNode .
				         { ?descNode solor:has_synonym ?synonym . }
				         UNION
				         { ?descNode solor:has_fully_qualified_name ?fqn . }
				     }
				     UNION
				     {
				         # Flatten Identifiers
				         ?concept solor:has_identifier ?idNode .
				         ?idNode solor:has_identifier_value ?idVal .
				         OPTIONAL {\s
				             ?idNode solor:has_identifier_system ?idSys .
				             ?idSys rdfs:label ?sysLabel .
				         }
				         BIND(IF(BOUND(?sysLabel), CONCAT(?sysLabel, ": ", ?idVal), ?idVal) AS ?formattedIdentifier)
				     }
				     UNION
				     {
				         # Flatten Hierarchy
				         ?concept solor:has_parent ?parent .
				         ?parent rdfs:label ?parentLabel .
				     }
				     UNION
				     {
				         ?concept solor:has_child ?child .
				         ?child rdfs:label ?childLabel .
				     }
				     UNION
				     {
				         # Map Relationships
				         ?concept ?predicateUuid ?objectUuid .
				         ?predicateUuid rdf:type rdf:Property .
				         ?predicateUuid rdfs:label ?predLabel .
				         ?objectUuid rdfs:label ?objectLabel .
				
				         BIND(IRI(CONCAT("https://www.ikm.dev/solor/", REPLACE(?predLabel, " ", "_"))) AS ?readablePredicate)
				     }
				 }
				""";

		// Execute the structural transformation
		try (QueryExecution transformExec = QueryExecutionFactory.create(transformQuery, context.getSolorModel())) {
			Model intermediateModel = transformExec.execConstruct();
			context.setSolorModel(intermediateModel);
		}
	}

	private void filterGraphRagModel(SolorGenerationContext context) {
		LOG.info("Filter graphRAG model");
		String filterQuery = """
				PREFIX solor: <https://www.ikm.dev/solor/>
				
				CONSTRUCT {
				    ?subject ?predicate ?object .
				}
				WHERE {
				    ?subject ?predicate ?object .
				
				    # 1. Prune orphan dictionary nodes:\s
				    # Only keep subjects that have actual clinical terminology descriptions
				    FILTER EXISTS {\s
				        ?subject solor:has_synonym | solor:has_fully_qualified_name []\s
				    }
				
				    # 2. Filter out redundant UUID strings from the identifiers
				    FILTER (\s
				        !(?predicate = solor:has_identifier && STRSTARTS(STR(?object), "UUID:"))\s
				    )
				}
				""";

		// Execute filtering against graphRAG model
		try (QueryExecution filterExec = QueryExecutionFactory.create(filterQuery, context.getSolorModel())) {
			Model finalRagModel = filterExec.execConstruct();
			context.setSolorModel(finalRagModel);
		}
	}

	private String convertSolorGraphToTurtleSyntax(Model solorGraph) {
		StringWriter writer = new StringWriter();
		solorGraph.write(writer, "TURTLE");
		return writer.toString();
	}

}
