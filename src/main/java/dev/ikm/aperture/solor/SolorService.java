package dev.ikm.aperture.solor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolorService {

	private static final Logger LOG = LoggerFactory.getLogger(SolorService.class);

	private final SolorPipeline solorPipeline;

	@Autowired
	public SolorService(SolorPipeline solorPipeline) {
		this.solorPipeline = solorPipeline;
	}

	public String constructSolorGraph(SolorRequest solorRequest) {
		return solorPipeline.execute(solorRequest);
	}

}
