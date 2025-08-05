package br.com.vamatos.product_api.modules.sales.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesProductsResponse {


    private List<String> salesId;
}
