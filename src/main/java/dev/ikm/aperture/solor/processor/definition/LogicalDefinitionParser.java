package dev.ikm.aperture.solor.processor.definition;

import dev.ikm.tinkar.component.graph.DiTree;
import dev.ikm.tinkar.entity.graph.DiTreeEntity;
import dev.ikm.tinkar.entity.graph.EntityVertex;
import dev.ikm.tinkar.terms.TinkarTermV2;
import dev.ikm.tinkar.terms.EntityProxy.Concept;


import java.util.ArrayList;
import java.util.List;

public class LogicalDefinitionParser {
	private final DiTreeEntity diTreeEntity;

	public LogicalDefinitionParser(DiTree<EntityVertex> diTree) {
		this.diTreeEntity = (DiTreeEntity) diTree;
	}

	public Definition parse() {
		Definition definition = new Definition();
		definition.sets(processDiTree());
		return definition;
	}


	private List<Clause> processDiTree() {
		List<Clause> clauses = new ArrayList<>();
		int[] definitionIndices = diTreeEntity.successorMap().get(diTreeEntity.root().vertexIndex()).toArray();
		for (int definitionIdx : definitionIndices) {
			Clause clause = buildClause(definitionIdx);
			clauses.add(clause);
		}
		return clauses;
	}

	private Clause buildClause(int definitionIdx) {
		Type type = determineClauseType(diTreeEntity.vertexMap().get(definitionIdx));
		Clause clause = new Clause(type);

		int[] successors = parseSuccessors(definitionIdx);
		for (int successorIdx : successors) {
			Type subType = determineSubType(successorIdx);
			switch (subType) {
				case ROLE_GROUP -> clause.addRoleGroup(buildRoleGroup(successorIdx));
				case ROLE -> clause.addRole(buildRole(successorIdx));
				case REFERENCE -> clause.addReference(buildReference(successorIdx));
				case null, default -> throw new IllegalStateException("Unknown sub type");
			}
		}

		return clause;
	}

	private RoleGroup buildRoleGroup(int roleGroupIdx) {
		RoleGroup roleGroup = new RoleGroup();
		int[] successors = parseSuccessors(roleGroupIdx);
		for (int roleIdx : successors) {
			roleGroup.addRole(buildRole(roleIdx));
		}
		return roleGroup;
	}

	private Role buildRole(int roleIdx) {
		Concept roleType = parseRoleTypeProperty(roleIdx);
		int referenceIdx = parseRoleReferenceIndex(roleIdx);
		Concept filler = parseConceptReference(referenceIdx);
		Reference reference = new Reference(filler);
		return new Role(roleType, reference);
	}

	private Reference buildReference(int referenceIdx) {
		Concept reference = parseConceptReference(referenceIdx);
		return new Reference(reference);
	}

	private Type determineSubType(int successorIdx) {
		EntityVertex andSuccessorVertex = diTreeEntity.vertexMap().get(successorIdx);
		//Check if the meaning is Concept Reference (aka Is-A)
		if (andSuccessorVertex.getMeaningNid() == TinkarTermV2.CONCEPT_REFERENCE.nid()) {
			return Type.REFERENCE;
		} else if (andSuccessorVertex.getMeaningNid() == TinkarTermV2.ROLE.nid()) {
			//Need to see if the Role Type property is Role Group or a Role constraint
			Concept roleType = parseRoleTypeProperty(successorIdx);
			if (roleType.nid() == TinkarTermV2.ROLE_GROUP.nid()) {
				return Type.ROLE_GROUP;
			} else {
				return Type.ROLE;
			}
		}
		return null;
	}

	private int[] parseSuccessors(int index) {
		int andIndex = diTreeEntity.successorMap().get(index).get(0);
		EntityVertex andVertex = diTreeEntity.vertexMap().get(andIndex);
		if (andVertex.getMeaningNid() != TinkarTermV2.AND.nid()) {
			throw new IllegalStateException("Expected AND node in single definition");
		}
		return diTreeEntity.successorMap().get(andIndex).toArray();
	}

	private Concept parseConceptReference(int index) {
		Concept[] roleProperties = diTreeEntity.vertexMap().get(index).properties().values().toArray(new Concept[0]);
		if (roleProperties.length != 1) {
			throw new IllegalStateException("Expected 1 property for Concept Reference");
		}
		return roleProperties[0];
	}

	private Concept parseRoleTypeProperty(int index) {
		EntityVertex vertex = diTreeEntity.vertexMap().get(index);
		Object roleType = vertex.properties().get(TinkarTermV2.ROLE_TYPE.nid());
		if (roleType instanceof Concept) {
			return (Concept) roleType;
		}
		throw new IllegalStateException("Expected ROLE_TYPE property for Role vertex " + index);
	}

	private int parseRoleReferenceIndex(int index) {
		int roleReferenceIndex = diTreeEntity.successorMap().get(index).get(0);
		EntityVertex roleReferenceVertex = diTreeEntity.vertexMap().get(roleReferenceIndex);
		if (roleReferenceVertex.getMeaningNid() != TinkarTermV2.CONCEPT_REFERENCE.nid()) {
			throw new IllegalStateException("Expected Concept Reference in role group in sufficient definition for " + roleReferenceVertex.getMeaningNid());
		}
		return roleReferenceIndex;
	}

	private Type determineClauseType(EntityVertex entityVertex) {
		if (entityVertex.getMeaningNid() == TinkarTermV2.NECESSARY_SET.nid()) {
			return Type.NECESSARY_SET;
		} else if (entityVertex.getMeaningNid() == TinkarTermV2.SUFFICIENT_SET.nid()) {
			return Type.SUFFICIENT_SET;
		} else {
			throw new IllegalStateException("Unknown definition type");
		}
	}
}
