package br.com.vamatos.product_api.modules.rabbitmq;

import br.com.vamatos.product_api.modules.product.dto.ProductStockDTO;
import br.com.vamatos.product_api.modules.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockListener {

    private final ProductService productService;

    @RabbitListener(queues = "${app-config.rabbit.queue.product-stock}")
    public void recieveProductStockMessage(ProductStockDTO product) throws JsonProcessingException {
        log.info("Recebendo mensagem: {} e transactionId: {}", new ObjectMapper().writeValueAsString(product), product.getTransactionId());
        productService.updateProductStock(product);
    }
}
