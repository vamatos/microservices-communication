package br.com.vamatos.product_api.modules.sales.rabbitmq;


import br.com.vamatos.product_api.modules.sales.dto.SalesConfirmationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class SalesConfirmationSender {


    private final RabbitTemplate rabbitTemplate;

    @Value("${app-config.rabbit.exchange.product}")
    private String productTopicExchange;

    @Value("${app-config.rabbit.routingKey.sales-confirmation}")
    private String salesConfirmationKey;


    public void sendSalesConirmationMessage(SalesConfirmationDTO message){
        try {
            log.info("Enviando mensagem: {}", new ObjectMapper().writeValueAsString(message));
            rabbitTemplate.convertAndSend(productTopicExchange, salesConfirmationKey, message);
            log.info("Mensagem enviada com sucesso!");
        } catch (Exception e) {
            log.error("Error while trying to send sales confirmation message: ", e);
        }

    }
}
