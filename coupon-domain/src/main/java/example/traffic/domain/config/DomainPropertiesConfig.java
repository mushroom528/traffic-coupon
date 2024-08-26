package example.traffic.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class DomainPropertiesConfig {

    @Profile({"local"})
    @PropertySource({"classpath:domain-local.properties", "classpath:domain.properties"})
    static class DomainProperties {

        @Bean
        public static PropertySourcesPlaceholderConfigurer PropertyConfig() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Profile({"test"})
    @PropertySource({"classpath:domain-test.properties", "classpath:domain.properties"})
    static class DomainTestProperties {

        @Bean
        public static PropertySourcesPlaceholderConfigurer PropertyConfig() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}
