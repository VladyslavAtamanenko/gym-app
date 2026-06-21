package com.epam.training.config;

import com.epam.training.filter.RequestResponseLoggingFilter;
import com.epam.training.filter.TransactionIdFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ AppConfig.class, PersistenceConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{ WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{ "/" };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter(
                "encoding-filter", new CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");

        // Must run before the logging filter so MDC is populated for all log statements
        FilterRegistration.Dynamic transactionFilter = servletContext.addFilter(
                "transaction-id-filter", new TransactionIdFilter());
        transactionFilter.addMappingForUrlPatterns(null, true, "/*");

        FilterRegistration.Dynamic loggingFilter = servletContext.addFilter(
                "rest-logging-filter", new RequestResponseLoggingFilter());
        loggingFilter.addMappingForUrlPatterns(null, true, "/*");
    }
}
