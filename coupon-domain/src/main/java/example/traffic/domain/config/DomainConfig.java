package example.traffic.domain.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EntityScan(basePackages = "example.traffic.domain")
@ComponentScan(basePackages = "example.traffic.domain")
@EnableJpaAuditing
@Import(QueryDslConfig.class)
public class DomainConfig {
}
