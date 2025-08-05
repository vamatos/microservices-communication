package br.com.vamatos.product_api.modules.product.controller;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.modules.product.dto.ProductCheckStockRequest;
import br.com.vamatos.product_api.modules.product.dto.ProductRequest;
import br.com.vamatos.product_api.modules.product.dto.ProductResponse;
import br.com.vamatos.product_api.modules.product.dto.ProductSalesResponse;
import br.com.vamatos.product_api.modules.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest productRequest) {
        return productService.save(productRequest);
    }

    @PutMapping("{id}")
    public ProductResponse update(@RequestBody ProductRequest productRequest, @PathVariable Integer id) {
        return productService.update(productRequest, id);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return productService.findByIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<ProductResponse> findByName(@PathVariable String name) {
        return productService.findByName(name);
    }

    @GetMapping("category/{id}")
    public List<ProductResponse> findByCategoryId(@PathVariable Integer id) {
        return productService.findByCategoryId(id);
    }

    @GetMapping("supplier/{id}")
    public List<ProductResponse> findBySupplier(@PathVariable Integer id) {
        return productService.findBySupplierId(id);
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return productService.delete(id);
    }

    @PostMapping("check-stock")
    public SuccessResponse checkProductsStock(@RequestBody ProductCheckStockRequest request){
        return productService.checkProductsStock(request);
    }

    @GetMapping("{id}/sales")
    public ProductSalesResponse findProductSales(@PathVariable Integer id){
        return productService.findProductsSales(id);
    }

}
