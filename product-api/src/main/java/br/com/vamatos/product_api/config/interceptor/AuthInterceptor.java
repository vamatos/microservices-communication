package br.com.vamatos.product_api.config.interceptor;

import br.com.vamatos.product_api.config.exception.ValidationException;
import br.com.vamatos.product_api.modules.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String X_TRANSACTION_ID="x-transaction-id";


    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (isOption(request)) {
            return true;
        }

        if(isEmpty(request.getHeader(X_TRANSACTION_ID))){
            throw new ValidationException("The x-transaction-id header is required");
        }

        var authorization = request.getHeader(AUTHORIZATION_HEADER);

        jwtService.validadeAuthorization(authorization);
        request.setAttribute("x-service-id", UUID.randomUUID().toString());
        return true;
    }

    private boolean isOption(HttpServletRequest request) {
        return HttpMethod.OPTIONS.name().equals(request.getMethod());
    }
}
