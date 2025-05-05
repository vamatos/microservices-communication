package br.com.vamatos.product_api.modules.supplier.service;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.config.exception.ValidationException;
import br.com.vamatos.product_api.modules.product.service.ProductService;
import br.com.vamatos.product_api.modules.supplier.dto.SupplierRequest;
import br.com.vamatos.product_api.modules.supplier.dto.SupplierResponse;
import br.com.vamatos.product_api.modules.supplier.model.Supplier;
import br.com.vamatos.product_api.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;

    public Supplier findById(Integer id){
        validateExistId(id);
        return supplierRepository.findById(id).orElseThrow(()-> new ValidationException("There's no supplier for the given ID"));
    }

    public SupplierResponse save(SupplierRequest request){
        this.validateSupplierNameInformed(request);
        var supplier = supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id) {
        this.validateSupplierNameInformed(request);
        var supplier = Supplier.of(request);
        this.validateExistId(id);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse findByIdResponse(Integer id){
        return SupplierResponse.of(findById(id));
    }


    public List<SupplierResponse> findAll(){
        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("The supplier name must be informed");
        }
        return supplierRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }


    private void validateSupplierNameInformed(SupplierRequest request){
        if(isEmpty(request.getName())){
            throw new ValidationException("The supplier name was not informed");
        }
    }

    public SuccessResponse delete(Integer id){
        validateExistId(id);
        if(productService.existsBySupplierId(id)){
            throw new ValidationException("You cannot delete this supplier beacuse it's already defined by a product.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("The supplier was deleted");
    }

    private void validateExistId(Integer id) {
        if(isEmpty(id)){
            throw new ValidationException("The supplier id must be informed");
        }
    }
}
