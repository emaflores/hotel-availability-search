package hotelavailability.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.oracle.OracleContainer;

// Single shared Oracle container for repository tests. Reuse speeds up local iterations a lot
// (first run ~30s, subsequent ~2s) — needs ~/.testcontainers.properties with testcontainers.reuse.enable=true.
@TestConfiguration(proxyBeanMethods = false)
public class OracleTestcontainersConfig {

    @Bean
    @ServiceConnection
    OracleContainer oracleContainer() {
        return new OracleContainer("gvenzl/oracle-free:23-slim-faststart")
                .withUsername("hotel")
                .withPassword("hotel")
                .withReuse(true);
    }
}
