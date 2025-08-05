package br.com.vamatos.product_api.config.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RequestResponseLogger {
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogger.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void log(Map<String, Object> params, String level) {
        try {
            String json = mapper.writeValueAsString(params);
            switch (level.toLowerCase()) {
                case "info": logger.info(json); break;
                case "error": logger.error(json); break;
                case "warn": logger.warn(json); break;
                default: logger.debug(json); break;
            }
        } catch (Exception e) {
            logger.error("Failed to log structured data", e);
        }
    }

    public static String mapStatusText(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) return "SUCCESS";
        if (statusCode >= 400 && statusCode < 500) return "CLIENT_ERROR";
        if (statusCode >= 500) return "SERVER_ERROR";
        return "UNKNOWN";
    }

    public static Map<String, Object> createBaseLog(
            String level,
            String message,
            String transactionId,
            String serviceId,
            String route,
            String method,
            int statusCode,
            long durationMs,
            String ip,
            String userId,
            Map<String, Object> data
    ) {
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", Instant.now().toString());
        log.put("level", level);
        log.put("message", message);
        log.put("transactionId", transactionId);
        log.put("serviceId", serviceId);
        log.put("route", route);
        log.put("method", method);
        log.put("status", statusCode);
        log.put("statusText", mapStatusText(statusCode));
        log.put("durationMs", durationMs);
        if (userId != null) log.put("userId", userId);
        if (ip != null) log.put("ip", ip);
        if (data != null) log.put("data", data);
        return log;
    }
}
