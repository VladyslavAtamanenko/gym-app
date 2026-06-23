package com.epam.training.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.epam.training.controller",
        "com.epam.training.exception.handler"
})
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new Jackson2HalModule());
        mapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(
                new DelegatingLinkRelationProvider(
                        new AnnotationLinkRelationProvider(),
                        new EvoInflectorLinkRelationProvider()),
                CurieProvider.NONE,
                MessageResolver.DEFAULTS_ONLY));
        converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagedResourcesAssemblerArgumentResolver(
                new HateoasPageableHandlerMethodArgumentResolver()));
    }
}
