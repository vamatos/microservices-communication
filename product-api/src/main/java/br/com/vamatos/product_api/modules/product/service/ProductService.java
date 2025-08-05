package br.com.vamatos.product_api.modules.product.service;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.config.exception.ValidationException;
import br.com.vamatos.product_api.config.logging.LogUtil;
import br.com.vamatos.product_api.config.logging.RequestResponseLogger;
import br.com.vamatos.product_api.modules.category.service.CategoryService;
import br.com.vamatos.product_api.modules.product.dto.*;
import br.com.vamatos.product_api.modules.product.model.Product;
import br.com.vamatos.product_api.modules.product.repository.ProductRepository;
import br.com.vamatos.product_api.modules.sales.client.SalesClient;
import br.com.vamatos.product_api.modules.sales.dto.SalesConfirmationDTO;
import br.com.vamatos.product_api.modules.sales.dto.SalesProductsResponse;
import br.com.vamatos.product_api.modules.sales.enums.SalesStatus;
import br.com.vamatos.product_api.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.vamatos.product_api.modules.supplier.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.vamatos.product_api.config.RequestUtil.getCurrentRequest;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
public class ProductService {

    private static final Integer ZERO = 0;
    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "x-transaction-id";
    private static final String SERVICE_ID = "x-service-id";

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final SalesConfirmationSender salesConfirmationSender;
    private final SalesClient salesClient;
    private final ObjectMapper objectMapper;

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

    public void updateProductStock(ProductStockDTO product) {
        try {
            validateProductStockUpdateData(product);
            updateStock(product);
        } catch (Exception e) {
            log.error("Error while trying to update stock for message with error: {}", e.getMessage(), e);
            var rejectedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED, product.getTransactionId());
            salesConfirmationSender.sendSalesConirmationMessage(rejectedMessage);
        }
    }

    @Transactional
    private void updateStock(ProductStockDTO product) {
        var productsForUpdate = new ArrayList<Product>();

        product
                .getProducts()
                .forEach(salesproduct -> {
                    var existingProduct = findById(salesproduct.getProductId());
                    validateQuantityInStock(salesproduct, existingProduct);
                    existingProduct.updateStock(salesproduct.getQuantity());
                    productsForUpdate.add(existingProduct);

                });
        if (!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APPROVED,product.getTransactionId());
            salesConfirmationSender.sendSalesConirmationMessage(approvedMessage);
        }

    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(String.format("The product %s is out of stock", existingProduct.getId()));
        }
    }


    private void validateProductStockUpdateData(ProductStockDTO product) {
        if (isEmpty(product) || isEmpty(product.getSalesId())) {
            throw new ValidationException("The product data and the sales id must be informed");
        }
        if (isEmpty(product.getProducts())) {
            throw new ValidationException("The sales' products must be informed");
        }
        product
                .getProducts()
                .forEach(salesproduct -> {
                    if (isEmpty(salesproduct.getProductId()) || isEmpty(salesproduct.getQuantity())) {
                        throw new ValidationException("The product id and the quantity must be informed");
                    }
                });
    }

    public ProductSalesResponse findProductsSales(Integer id) {
        var product = findById(id);
        var sales = getSalesByProductId(product.getId());
        return ProductSalesResponse.of(product, sales.getSalesId());
    }


    private SalesProductsResponse getSalesByProductId(Integer productId) {
        try {

            var currentRequest = getCurrentRequest();
            var token = currentRequest.getHeader(AUTHORIZATION);
            var transactionid = currentRequest.getHeader(TRANSACTION_ID);
//            var serviceid = currentRequest.getAttribute(SERVICE_ID);
            LogUtil.logInfo("Sending GET request to orders by productId",null,productId);
            var response = salesClient
                    .findSalesByProductId(productId, token, transactionid)
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product."));
            LogUtil.logInfo("Recieving response from orders by productId",null,objectMapper.writeValueAsString(response));

            return response;
        } catch (Exception ex) {
            log.error("Error trying to call Sales-API: {}", ex.getMessage());
            throw new ValidationException("The sales could not be found.");
        }
    }

    public SuccessResponse checkProductsStock(ProductCheckStockRequest request){
        var currentRequest = getCurrentRequest();
        var transactionid = currentRequest.getHeader(TRANSACTION_ID);
        var serviceid = currentRequest.getAttribute(SERVICE_ID);
        log.info("Request to POST");
        if(isEmpty(request) || isEmpty(request.getProducts())){
            throw new ValidationException("The request data must be informed");
        }
        request
                .getProducts()
                .forEach(this::validateStock);

        return SuccessResponse.create("The stock is ok!");
    }

    private void validateStock(ProductQuantityDTO productQuantityDTO){
        if(isEmpty(productQuantityDTO.getProductId())|| isEmpty(productQuantityDTO.getQuantity())){
            throw new ValidationException("Product ID and quantity must be informed");
        }

            var product = findById(productQuantityDTO.getProductId());
        if(productQuantityDTO.getQuantity()> product.getQuantityAvailable() ){
            throw new ValidationException(String.format("The product %s is out of stock", product.getId()));
        }
    }


}
