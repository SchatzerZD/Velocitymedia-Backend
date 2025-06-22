package no.velocitymedia.velocitymedia_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/videos/**")
            .addResourceLocations("file:/var/data/media/videos/")
            .setCachePeriod(3600);

        registry.addResourceHandler("/media/images/**")
            .addResourceLocations("file:/var/data/media/images/")
            .setCachePeriod(3600);

        registry.addResourceHandler("/media/contracts/**")
            .addResourceLocations("file:/var/data/media/contracts/")
            .setCacheControl(CacheControl.noCache());
    }

}
