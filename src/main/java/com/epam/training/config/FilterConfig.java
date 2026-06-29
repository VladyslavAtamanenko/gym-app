package com.epam.training.config;

import com.epam.training.filter.RequestResponseLoggingFilter;
import com.epam.training.filter.TransactionIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilter() {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>(new TransactionIdFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> reg = new FilterRegistrationBean<>(new RequestResponseLoggingFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return reg;
    }
}
