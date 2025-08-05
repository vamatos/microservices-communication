package br.com.vamatos.product_api.config.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest originalRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        CachedBodyHttpServletRequest request = new CachedBodyHttpServletRequest(originalRequest);

        long start = System.currentTimeMillis();
        String transactionId = Optional.ofNullable(request.getHeader("x-transaction-id"))
                .orElse(UUID.randomUUID().toString());

        String serviceId = Optional.ofNullable(request.getHeader("x-service-id"))
                .orElse(UUID.randomUUID().toString());

        request.setAttribute("transactionId", transactionId);
        request.setAttribute("serviceId", serviceId);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("headers", Collections.list(request.getHeaderNames())
                .stream()
                .collect(HashMap::new, (m, h) -> m.put(h, request.getHeader(h)), HashMap::putAll));
        requestData.put("query", request.getQueryString());

        if (isBodyReadable(request.getMethod())) {
            requestData.put("body", request.getCachedBodyAsString());
        }

        RequestResponseLogger.log(
                RequestResponseLogger.createBaseLog(
                        "info",
                        "Request received",
                        transactionId,
                        serviceId,
                        request.getRequestURI(),
                        request.getMethod(),
                        0,
                        0,
                        request.getRemoteAddr(),
                        null,
                        requestData
                ),
                "info"
        );

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("contentType", response.getContentType());

            RequestResponseLogger.log(
                    RequestResponseLogger.createBaseLog(
                            "info",
                            "Response sent",
                            transactionId,
                            serviceId,
                            request.getRequestURI(),
                            request.getMethod(),
                            response.getStatus(),
                            duration,
                            request.getRemoteAddr(),
                            null,
                            responseData
                    ),
                    "info"
            );
        }
    }

    private boolean isBodyReadable(String method) {
        return method.equalsIgnoreCase("POST")
                || method.equalsIgnoreCase("PUT")
                || method.equalsIgnoreCase("PATCH");
    }
}

