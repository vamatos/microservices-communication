package br.com.vamatos.product_api.config.interceptor;

import br.com.vamatos.product_api.config.exception.ValidationException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public class FeignClientAuthInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION = "Authorization";


    @Override
    public void apply(RequestTemplate requestTemplate) {
        var currentRequest = getCurrentRequest();
        requestTemplate.header(AUTHORIZATION, currentRequest.getHeader(AUTHORIZATION));


    }


    private HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                    .getRequestAttributes()))
                    .getRequest();
        } catch (Exception e) {
            throw new ValidationException("The current request could not be proccessed.");
        }
    }
}
