package dev.ikm.aperture.solor.processor.definition;

import java.util.ArrayList;
import java.util.List;

public class Definition {

	private List<Clause> clauses;

	public Definition() {
		this.clauses = new ArrayList<>();
	}

	public long sufficientSetCount() {
		return clauses.stream().filter(clause -> clause.element() == Type.SUFFICIENT_SET).count();
	}

	public long necessarySetCount() {
		return clauses.stream().filter(clause -> clause.element() == Type.NECESSARY_SET).count();
	}

	public List<Clause> sets() {
		return clauses;
	}

	public void sets(List<Clause> clauses) {
		this.clauses = clauses;
	}

	public void addSet(Clause clause) {
		clauses.add(clause);
	}
}
