package br.com.vamatos.product_api.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductRequest {

    private Integer id;
    private String name;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    private Integer quantityAvailable;
    private Integer supplierId;
    private Integer categoryId;
}
