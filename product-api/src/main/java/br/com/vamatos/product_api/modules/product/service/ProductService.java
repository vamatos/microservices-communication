package br.com.vamatos.product_api.modules.product.service;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.config.exception.ValidationException;
import br.com.vamatos.product_api.modules.category.service.CategoryService;
import br.com.vamatos.product_api.modules.product.dto.ProductRequest;
import br.com.vamatos.product_api.modules.product.dto.ProductResponse;
import br.com.vamatos.product_api.modules.product.model.Product;
import br.com.vamatos.product_api.modules.product.repository.ProductRepository;
import br.com.vamatos.product_api.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    public ProductResponse save(ProductRequest request) {
        this.validateProductDataInformed(request);
        this.validateCategoryandSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = productRepository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id) {
        this.validateProductDataInformed(request);
        this.validateExistId(id);
        this.validateCategoryandSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());

        var product = Product.of(request, supplier, category);
        product.setId(id);
        productRepository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }

    public Product findById(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("the product was not informed");
        }
        return productRepository.findById(id).orElseThrow(() -> new ValidationException("There's no product for the given ID"));
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }


    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("The product name must be informed");
        }
        return productRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The category id must be informed");
        }
        return productRepository.findByCategoryId(id)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The supplier id must be informed");
        }
        return productRepository.findBySupplierId(id)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }


    private void validateProductDataInformed(ProductRequest request) {
        if (isEmpty(request.getName())) {
            throw new ValidationException("The product name was not informed");
        }
        if (isEmpty(request.getQuantityAvailable())) {
            throw new ValidationException("The quantity available was not informed");
        }
        if (request.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity available SHOULD NOT BE LESS OR EQUAL TO ZERO");
        }
    }

    private void validateCategoryandSupplierIdInformed(ProductRequest request) {
        if (isEmpty(request.getCategoryId())) {
            throw new ValidationException("Ther category Id  was not informed");
        }
        if (isEmpty(request.getSupplierId())) {
            throw new ValidationException("Ther supplier Id  was not informed");
        }
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    public SuccessResponse delete(Integer id) {
        validateExistId(id);
        productRepository.deleteById(id);
        return SuccessResponse.create("The product was deleted");
    }

    private void validateExistId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The product id must be informed");
        }
    }
}
