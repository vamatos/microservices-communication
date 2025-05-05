package br.com.vamatos.product_api.modules.category.repository;

import br.com.vamatos.product_api.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    List<Category> findByDescriptionIgnoreCaseContaining(String description);
}
