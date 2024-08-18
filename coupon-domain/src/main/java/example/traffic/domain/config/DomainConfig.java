package example.traffic.domain.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "example.traffic.domain")
@Import({QueryDslConfig.class, DomainRepositoryConfig.class, DomainPropertiesConfig.class})
public class DomainConfig {
}
