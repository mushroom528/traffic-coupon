package example.traffic.web.config;

import example.traffic.application.config.ApplicationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApplicationConfig.class})
public class WebConfig {
}
