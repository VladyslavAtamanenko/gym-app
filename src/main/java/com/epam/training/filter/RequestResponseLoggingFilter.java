package com.epam.training.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestResponseLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_BODY_LENGTH = 1000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper((jakarta.servlet.http.HttpServletResponse) response);

        long start = System.currentTimeMillis();
        logRequest(wrappedRequest);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logResponse(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String query = request.getQueryString();
        String uri = query != null ? request.getRequestURI() + "?" + query : request.getRequestURI();
        log.info(">> {} {}", request.getMethod(), uri);
    }

    private void logResponse(ContentCachingRequestWrapper request,
                             ContentCachingResponseWrapper response,
                             long durationMs) {
        int status = response.getStatus();
        log.info("<< {} {} -> {} ({}ms)", request.getMethod(), request.getRequestURI(), status, durationMs);

        if (log.isDebugEnabled()) {
            String requestBody = bodyOf(request.getContentAsByteArray());
            if (!requestBody.isEmpty()) {
                log.debug("Request body: {}", truncate(requestBody));
            }
        }

        String responseBody = bodyOf(response.getContentAsByteArray());
        if (!responseBody.isEmpty()) {
            if (status >= 400) {
                log.info("Response body: {}", truncate(responseBody));
            } else if (log.isDebugEnabled()) {
                log.debug("Response body: {}", truncate(responseBody));
            }
        }
    }

    private String bodyOf(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        return new String(bytes, StandardCharsets.UTF_8).strip();
    }

    private String truncate(String s) {
        return s.length() <= MAX_BODY_LENGTH ? s : s.substring(0, MAX_BODY_LENGTH) + "…[truncated]";
    }
}
