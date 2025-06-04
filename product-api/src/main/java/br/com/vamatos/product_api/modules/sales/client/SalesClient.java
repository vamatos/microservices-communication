package br.com.vamatos.product_api.modules.sales.client;


import br.com.vamatos.product_api.modules.sales.dto.SalesProductsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "salesClient",
        contextId = "salesClient",
        url = "${app-config.services.sales}"
)
public interface SalesClient {


    @GetMapping("/products/{productId}")
    Optional<SalesProductsResponse> findSalesByProductId(@PathVariable Integer productId);
}
