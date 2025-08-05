package br.com.vamatos.product_api.config.logging;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static br.com.vamatos.product_api.config.RequestUtil.getCurrentRequest;

public class LogUtil {
    private static final String TRANSACTION_ID = "x-transaction-id";
    private static final String SERVICE_ID = "x-service-id";

    private static String getStatusText(int status) {
        return switch (status / 100) {
            case 1 -> "INFORMATIONAL";
            case 2 -> "SUCCESS";
            case 3 -> "REDIRECTION";
            case 4 -> "CLIENT_ERROR";
            case 5 -> "SERVER_ERROR";
            default -> "UNKNOWN";
        };
    }

    private static Map<String, Object> baseLog(
            String level,
            String message,
            String transactionId,
            String route,
            String method,
            int status,
            long duration,
            String ip,
            String userId,
            Object data
    ) {
        var currentRequest = getCurrentRequest();
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", Instant.now().toString());
        log.put("level", level);
        log.put("message", message);
        log.put("transactionId", transactionId != null ? transactionId : currentRequest.getHeader(TRANSACTION_ID));
        log.put("serviceId", transactionId != null ? transactionId : currentRequest.getHeader(SERVICE_ID));
        log.put("route", route);
        log.put("method", method);
        log.put("status", status);
        log.put("statusText", getStatusText(status));
        log.put("durationMs", duration);
        log.put("ip", ip);
        log.put("userId", userId);
        log.put("data", data != null ? data : Map.of());
        return log;
    }

    public static void logInfo(String message, String transactionId, Object data) {
        RequestResponseLogger.log(
                baseLog("info", message, transactionId, "", "", 0, 0, "", "", data), "info"
        );
    }

    public static void logSuccess(String message, String transactionId, String route, String method,
                                  int status, long duration, String ip, String userId,  Object data) {
        RequestResponseLogger.log(
                baseLog("info", message, transactionId, route, method, status, duration, ip, userId, data), "info"
        );
    }

    public static void logWarn(String message, String transactionId, Map<String, Object> data) {
        RequestResponseLogger.log(
                baseLog("warn", message, transactionId, "", "", 0, 0, "", "", data), "info"
        );
    }

    public static void logError(String message, String transactionId, String route, String method,
                                int status, String ip, String userId, Map<String, Object> data) {
        RequestResponseLogger.log(
                baseLog("error", message, transactionId, route, method, status, 0, ip, userId, data), "error"
        );
    }

}
