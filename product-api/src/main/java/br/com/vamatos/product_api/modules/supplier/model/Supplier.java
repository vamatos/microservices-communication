package br.com.vamatos.product_api.modules.supplier.model;


import br.com.vamatos.product_api.modules.supplier.dto.SupplierRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SUPPLIER")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    public static Supplier of(SupplierRequest request){
        var supplier = new Supplier();
        BeanUtils.copyProperties(request, supplier);
        return supplier;
    }
}
