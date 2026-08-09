package dev.ikm.aperture.solor.processor;

import dev.ikm.aperture.solor.SolorGenerationContext;
import dev.ikm.aperture.solor.SolorRequest;
import dev.ikm.aperture.solor.processor.definition.Definition;
import dev.ikm.aperture.solor.processor.definition.LogicalDefinitionParser;
import dev.ikm.aperture.solor.processor.definition.Role;
import dev.ikm.tinkar.common.id.IntIdSet;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.component.graph.DiTree;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityHandle;
import dev.ikm.tinkar.entity.graph.DiTreeEntity;
import dev.ikm.tinkar.entity.graph.EntityVertex;
import dev.ikm.tinkar.terms.TinkarTermV2;
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
					Set<Role> roles = new HashSet<>();

					// Collect all Roles from Definition
					new LogicalDefinitionParser(diTree).parse()
							.sets().forEach(clause -> {
								roles.addAll(clause.roles());
								clause.roleGroups().forEach(roleGroup -> roles.addAll(roleGroup.roles()));
							});

					// For each Role construct the appropriate RDF Triples
					roles.forEach(role -> {
						// Create RDF triples for each Role
						Resource subject = model.createResource(SolorVocabulary.NAMESPACE + conceptUUID);
						Property predicate = model.createProperty(SolorVocabulary.NAMESPACE + role.predicate().asUuidList().get(0));
						Resource object = model.createResource(SolorVocabulary.NAMESPACE + role.reference().concept().asUuidList().get(0));

						// Attach RDF triple to conceptId
						subject.addProperty(predicate, object);

						// Add dependant concepts to context
						solorGenerationContext.requireTargetConcept(role.predicate());
						solorGenerationContext.requireTargetConcept(role.reference().concept());
					});

				});
	}
}
