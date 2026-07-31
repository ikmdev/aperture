package dev.ikm.aperture.ike;

import dev.ikm.tinkar.common.service.*;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class IkeDatabaseConfig {

	private final static Logger LOG = LoggerFactory.getLogger(IkeDatabaseConfig.class);

	@Value("${app.database.directory}")
	private File databaseDirectory;

	@PostConstruct
	public void start() {
		LOG.info("Database initialization started");
		CachingService.clearAll();
		LOG.info("Clear database cache");

		if (!databaseDirectory.exists()) {
			LOG.info("Data directory does not exist. Creating it at: {}", databaseDirectory.getAbsolutePath());
			boolean created = databaseDirectory.mkdirs();
			if (!created) {
				throw new RuntimeException("Failed to create data directory: " + databaseDirectory.getAbsolutePath());
			}
		}

		if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
			ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, databaseDirectory);
			PrimitiveData.selectControllerByName("Open SpinedArrayStore");

			// Log useful JVM information
			LOG.info("JVM Version: {}", System.getProperty("java.version"));
			LOG.info("JVM Name: {}", System.getProperty("java.vm.name"));
			LOG.info(ServiceProperties.jvmUuid());

			// Start database
			PrimitiveData.start();

			LOG.info("Database initialization completed");
		} else {
			throw new RuntimeException("Data directory does not exist: " + databaseDirectory.getAbsolutePath());
		}
	}

	@PreDestroy
	public void shutdown() {
		LOG.info("Database shutdown started");
		PrimitiveData.stop();
		LOG.info("Database shutdown completed");
	}

	@Bean
	public PrimitiveDataService getPrimitiveDataService() {
		return PrimitiveData.get();
	}

	@Bean
	EntityService getEntityService() {
		return Entity.provider();
	}
}
