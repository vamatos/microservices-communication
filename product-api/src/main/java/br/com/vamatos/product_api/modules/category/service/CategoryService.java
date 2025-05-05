package br.com.vamatos.product_api.modules.category.service;


import br.com.vamatos.product_api.config.exception.SuccessResponse;
import br.com.vamatos.product_api.config.exception.ValidationException;
import br.com.vamatos.product_api.modules.category.dto.CategoryRequest;
import br.com.vamatos.product_api.modules.category.dto.CategoryResponse;
import br.com.vamatos.product_api.modules.category.model.Category;
import br.com.vamatos.product_api.modules.category.repository.CategoryRepository;
import br.com.vamatos.product_api.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public CategoryResponse save(CategoryRequest request){
        this.validateCategoryNameInformed(request);
        var category = categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request, Integer id){
        this.validateCategoryNameInformed(request);
        this.validateExistId(id);
        var category = Category.of(request);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    public CategoryResponse findByIdResponse(Integer id){
        return CategoryResponse.of(findById(id));
    }


    public List<CategoryResponse> findAll(){
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public Category findById(Integer id){
        validateExistId(id);
        return categoryRepository.findById(id).orElseThrow(()-> new ValidationException("There's no category for the given ID"));
    }
    public List<CategoryResponse> findByDescription(String description){
        if(isEmpty(description)){
            throw new ValidationException("The category description must be informed");
        }
        return categoryRepository.findByDescriptionIgnoreCaseContaining(description)
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }


    private void validateCategoryNameInformed(CategoryRequest request){
        if(isEmpty(request.getDescription())){
            throw new ValidationException("Ther category description was not informed");
        }
    }

    public SuccessResponse delete(Integer id){
        validateExistId(id);
        if(productService.existsByCategoryId(id)){
            throw new ValidationException("You cannot delete this category because it's already defined by a product.");
        }
        categoryRepository.deleteById(id);
        return SuccessResponse.create("The category was deleted");
    }

    private void validateExistId(Integer id) {
        if(isEmpty(id)){
            throw new ValidationException("The supplier id must be informed");
        }
    }
}
