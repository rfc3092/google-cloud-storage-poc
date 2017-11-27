package no.nav.pam.cloud.storage;

import no.nav.pam.image.ImageDownscaler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudStorageConfig {

    @Value("${google.cloud.project}")
    private String project;

    @Value("${google.cloud.privateKey.id}")
    private String privateKeyId;

    @Value("${google.cloud.privateKey.content}")
    private String privateKey;

    @Value("${google.cloud.client.id}")
    private String clientId;

    @Value("${google.cloud.client.email}")
    private String clientEmail;

    @Value("${google.cloud.storage.bucket}")
    private String bucket;

    @Bean
    public CloudStorageGateway cloudStorageGateway(ImageDownscaler downscaler)
            throws CloudStorageException {
        return new CloudStorageGateway(clientId, clientEmail, privateKey, privateKeyId, project, bucket, downscaler);
    }

}
