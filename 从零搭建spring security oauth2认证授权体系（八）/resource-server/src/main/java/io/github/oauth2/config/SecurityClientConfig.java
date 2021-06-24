package io.github.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(
        prefix = "oauth2"
)
public class SecurityClientConfig {
    private String clientId;
    private String clientSecret;
    private String checkTokenUri;
}
