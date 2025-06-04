package br.com.vamatos.product_api.modules.product.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCheckStockRequest {

    List<ProductQuantityDTO> products;
}
