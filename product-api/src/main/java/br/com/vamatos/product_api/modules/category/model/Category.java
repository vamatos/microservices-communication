package br.com.vamatos.product_api.modules.category.model;

import br.com.vamatos.product_api.modules.category.dto.CategoryRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    public static Category of(CategoryRequest request){
        var category = new Category();
        BeanUtils.copyProperties(request, category);
        return category;
    }
}
