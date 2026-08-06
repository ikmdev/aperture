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

	public SolorGenerationContext(SolorRequest solorRequest, Model solorModel) {
		this.solorRequest = solorRequest;
		this.solorModel = solorModel;
		this.processedConceptIds = new HashSet<>();
		this.pendingConceptIds = new HashSet<>();
	}

	public Model getSolorModel() {
		return solorModel;
	}

	public SolorRequest getSolorRequest() {
		return solorRequest;
	}

	public Set<PublicId> getProcessedConceptIds() {
		return processedConceptIds;
	}

	public void requireTargetConcept(PublicId conceptId) {
		if (!processedConceptIds.contains(conceptId)) {
			pendingConceptIds.add(conceptId);
		}
	}

	public Set<PublicId> getAndClearPendingConceptIds() {
		Set<PublicId> batch = new HashSet<>(pendingConceptIds);
		processedConceptIds.addAll(batch);
		pendingConceptIds.clear();
		return batch;
	}
}
