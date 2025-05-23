package br.com.vamatos.product_api.modules.supplier.controller;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.modules.supplier.dto.SupplierRequest;
import br.com.vamatos.product_api.modules.supplier.dto.SupplierResponse;
import br.com.vamatos.product_api.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/supplier")
public class SupplierController {


    @Autowired
    private SupplierService supplierService;


    @PostMapping
    public SupplierResponse save(@RequestBody SupplierRequest supplierRequest) {
        return supplierService.save(supplierRequest);
    }

    @PutMapping("{id}")
    public SupplierResponse update(@RequestBody SupplierRequest supplierRequest, @PathVariable Integer id) {
        return supplierService.update(supplierRequest, id);
    }

    @GetMapping
    public List<SupplierResponse> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("{id}")
    public SupplierResponse findById(@PathVariable Integer id) {
        return supplierService.findByIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<SupplierResponse> findByName(@PathVariable String name) {
        return supplierService.findByName(name);
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return supplierService.delete(id);
    }

}
