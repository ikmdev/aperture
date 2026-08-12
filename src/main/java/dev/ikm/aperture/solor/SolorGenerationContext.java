package dev.ikm.aperture.solor;

import dev.ikm.tinkar.common.id.PublicId;
import org.apache.jena.rdf.model.Model;

import java.util.HashSet;
import java.util.Set;

public class SolorGenerationContext {

	private final SolorRequest solorRequest;
	private final Model solorModel;
	private final Set<PublicId> processedConceptIds;
	private final Set<PublicId> pendingConceptIds;
	private final Set<PublicId> processedPredicateIds;
	private final Set<PublicId> pendingPredicateIds;

	public SolorGenerationContext(SolorRequest solorRequest, Model solorModel) {
		this.solorRequest = solorRequest;
		this.solorModel = solorModel;
		this.processedConceptIds = new HashSet<>();
		this.pendingConceptIds = new HashSet<>();
		this.processedPredicateIds = new HashSet<>();
		this.pendingPredicateIds = new HashSet<>();
	}

	public Model getSolorModel() {
		return solorModel;
	}

	public SolorRequest getSolorRequest() {
		return solorRequest;
	}

	public void requireConcept(PublicId conceptId) {
		if (!processedConceptIds.contains(conceptId)) {
			pendingConceptIds.add(conceptId);
		}
	}

	public void requirePredicate(PublicId conceptId) {
		requireConcept(conceptId);
		if (!processedPredicateIds.contains(conceptId)) {
			pendingPredicateIds.add(conceptId);
		}
	}

	public Set<PublicId> getAndClearPendingConceptIds() {
		Set<PublicId> batch = new HashSet<>(pendingConceptIds);
		processedConceptIds.addAll(batch);
		pendingConceptIds.clear();
		return batch;
	}

	public Set<PublicId> getAndClearPendingPredicateIds() {
		Set<PublicId> batch = new HashSet<>(pendingPredicateIds);
		processedPredicateIds.addAll(batch);
		pendingPredicateIds.clear();
		return batch;
	}

}
