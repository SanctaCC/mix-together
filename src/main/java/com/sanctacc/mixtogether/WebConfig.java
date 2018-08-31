package com.sanctacc.mixtogether;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*").allowedMethods("*")
                .allowedOrigins("*");
    }

    private PathResourceResolver getCustomResourceResolver() {
        return new PathResourceResolver() {
            @Override
            protected Resource getResource(String resourcePath,
                                           Resource location) throws IOException {
                Resource superR = super.getResource(resourcePath, location);
                return (superR != null) ? superR
                        : new ClassPathResource("static/index.html");
            }
        };
    }

    private HttpRequestHandler customResourceHttpRequestHandler(ApplicationContext context) throws Exception {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setLocationValues(Collections.singletonList("classpath:/static/"));
        handler.setResourceResolvers(Collections.singletonList(getCustomResourceResolver()));
        handler.setResourceTransformers(Collections.singletonList(new AppCacheManifestTransformer()));
//        handler.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
        handler.setCacheSeconds(0); //DEV
        String[] allHTTPMethods = Arrays.stream(RequestMethod.values()).map(Enum::toString).toArray(String[]::new);
        //return index.html for every possible request except api calls
        handler.setSupportedMethods(allHTTPMethods);
        handler.setApplicationContext(context);
        handler.afterPropertiesSet();
        return handler;
    }

    @Bean
    @ConditionalOnResource(resources = "classpath:/static/index.html")
    public HandlerMapping customHandlerMapping(ApplicationContext applicationContext) throws Exception {
        HttpRequestHandler handler = customResourceHttpRequestHandler(applicationContext);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        //api calls should always have priority so set lowest possible precedence
        handlerMapping.setOrder(Integer.MAX_VALUE - 1);
        handlerMapping.setUrlMap(Collections.singletonMap("/**", handler));
        return handlerMapping;
    }
}