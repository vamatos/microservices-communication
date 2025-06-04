package br.com.vamatos.product_api.modules.category.controller;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.modules.category.dto.CategoryRequest;
import br.com.vamatos.product_api.modules.category.dto.CategoryResponse;
import br.com.vamatos.product_api.modules.category.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.save(categoryRequest);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("{id}")
    public CategoryResponse findById(@PathVariable Integer id) {
        return categoryService.findByIdResponse(id);
    }

    @GetMapping("description/{description}")
    public List<CategoryResponse> findByDescription(@PathVariable String description) {
        return categoryService.findByDescription(description);
    }

    @PutMapping("{id}")
    public CategoryResponse update(@PathVariable Integer id,
                                   @RequestBody CategoryRequest categoryRequest) {
        return categoryService.update(categoryRequest, id);
    }


    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }

}
