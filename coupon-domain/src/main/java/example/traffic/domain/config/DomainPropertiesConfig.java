package example.traffic.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class DomainPropertiesConfig {

    @PropertySource({"classpath:domain.properties"})
    static class DomainProperties {

        @Bean
        public static PropertySourcesPlaceholderConfigurer PropertyConfig() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}
