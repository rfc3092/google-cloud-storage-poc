package no.nav.pam.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageDownscalerConfig {

    @Value("${image.size.max.x}")
    private int maxSizeX;

    @Value("${image.size.max.y}")
    private int maxSizeY;

    @Bean
    public ImageDownscaler imageReducer() {
        return new ImageDownscaler(maxSizeX, maxSizeY);
    }

}
