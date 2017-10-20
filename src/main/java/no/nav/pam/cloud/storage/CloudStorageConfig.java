package no.nav.pam.cloud.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudStorageConfig {

    @Value("${google.cloud.storage.bucket}")
    private String bucket;

    @Bean
    public CloudStorageGateway cloudStorageGateway() {
        return new CloudStorageGateway(bucket);
    }

}
