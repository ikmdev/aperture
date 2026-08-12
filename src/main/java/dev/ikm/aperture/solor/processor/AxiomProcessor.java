package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.aperture.solor.processor.definition.LogicalDefinitionParser;
import dev.ikm.aperture.solor.processor.definition.Role;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.component.graph.DiTree;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.graph.EntityVertex;
import dev.ikm.tinkar.entity.graph.adaptor.axiom.LogicalAxiom;
import dev.ikm.tinkar.entity.graph.adaptor.axiom.LogicalAxiomAdaptor;
import dev.ikm.tinkar.entity.graph.adaptor.axiom.LogicalExpression;
import dev.ikm.tinkar.terms.TinkarTermV2;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AxiomProcessor implements KnowledgeProcessor {

	@Override
	public void process(SolorGenerationContext solorGenerationContext, PublicId conceptId) {
		SolorRequest solorRequest = solorGenerationContext.getSolorRequest();
		int nid = Entity.nid(conceptId);
		UUID conceptUUID = conceptId.asUuidList().get(0);
		Model model = solorGenerationContext.getSolorModel();

		solorRequest.stampCalculatorWithCache().forEachSemanticVersionForComponentOfPattern(
				nid,
				TinkarTermV2.EL_PLUS_PLUS_INFERRED_AXIOMS_PATTERN.nid(),
				(semanticEntityVersion, _, _) -> {
					DiTree<EntityVertex> diTree = semanticEntityVersion.fieldAsDiTree(0);
					LogicalExpression logicalExpression = new LogicalExpression(diTree);

					Resource subject = model.createResource(SolorVocabulary.NAMESPACE + conceptUUID);

					logicalExpression.nodesOfType(LogicalAxiom.Atom.TypedAtom.class).forEach(typeAtom -> {
						switch (typeAtom) {
							case LogicalAxiom.Atom.TypedAtom.Role role -> {
								if (role.restriction() instanceof LogicalAxiom.Atom.ConceptAxiom conceptAxiom) {
									UUID predicateId = role.type().publicId().asUuidList().get(0);
									UUID objectId = conceptAxiom.concept().publicId().asUuidList().get(0);

									// Create RDF triples for each Role
									Property predicate = model.createProperty(SolorVocabulary.NAMESPACE + predicateId);
									Resource object = model.createResource(SolorVocabulary.NAMESPACE + objectId);

									// Attach RDF triple to conceptId
									subject.addProperty(predicate, object);

									// Add dependent concepts to context
									solorGenerationContext.requirePredicate(PublicIds.of(predicateId));
									solorGenerationContext.requireConcept(PublicIds.of(objectId));
								}
							}
							case LogicalAxiom.Atom.TypedAtom.Feature feature -> {

								UUID featureTypeId = feature.type().publicId().asUuidList().get(0);
								UUID concreteOperatorId = feature.concreteDomainOperator().publicId().asUuidList().get(0);
								String literalValue = String.valueOf(feature.literal());


								// Create Feature node
								Resource featureNode = model.createResource()
										.addProperty(SolorVocabulary.HAS_FEATURE_TYPE, model.createResource(SolorVocabulary.NAMESPACE + featureTypeId))
										.addProperty(SolorVocabulary.HAS_FEATURE_CONCRETE_OPERATOR, model.createResource(SolorVocabulary.NAMESPACE + concreteOperatorId))
										.addProperty(SolorVocabulary.HAS_FEATURE_VALUE, model.createLiteral(literalValue));

								// Attach RDF triple to conceptId
								subject.addProperty(SolorVocabulary.HAS_FEATURE, featureNode);

								// Add dependent concepts to context
								solorGenerationContext.requirePredicate(PublicIds.of(featureTypeId));
								solorGenerationContext.requirePredicate(PublicIds.of(concreteOperatorId));
							}
							case null, default -> throw new IllegalStateException("Unexpected value: " + typeAtom);
						}

					});
				});
	}
}
