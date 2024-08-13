package example.traffic.application.config;

import example.traffic.domain.config.DomainConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "example.traffic.application")
@Import(DomainConfig.class)
public class ApplicationConfig {
}
