package dev.ikm.aperture.solor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class RdfService {

	private static final Logger LOG = LoggerFactory.getLogger(RdfService.class);

	private static final String SOLOR_NS = "https://www.ikm.dev/solor/";

	private final Model baseOntModelTemplate;

	public RdfService() {
		this.baseOntModelTemplate = ModelFactory.createDefaultModel();
		this.baseOntModelTemplate.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.baseOntModelTemplate.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		this.baseOntModelTemplate.setNsPrefix("solor", SOLOR_NS);
	}

	public String generateEmptySolorGraph() {
		LOG.info("Generating empty solor graph");
		Model solorGraph = ModelFactory.createDefaultModel().add(baseOntModelTemplate);
		return convertModelToTurtle(solorGraph);
	}

	public String generateSolorGraph(SolorRequest solorRequest) {
		LOG.info("Generating solor graph");
		Model solorGraph = ModelFactory.createDefaultModel().add(baseOntModelTemplate);
		return convertModelToTurtle(solorGraph);
	}

	private String convertModelToTurtle(Model solorGraph) {
		StringWriter writer = new StringWriter();
		solorGraph.write(writer, "TURTLE");
		return writer.toString();
	}
}
