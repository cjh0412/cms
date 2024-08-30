package com.cjhdev.cms.order.domain.model;


import com.cjhdev.cms.order.domain.product.AddProductForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited // 엔티티 이력 관리(entity_aud 테이블 자동생성)
@AuditOverride(forClass = BaseEntity.class)
//@Setter
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL) // 1:N 관계 설정
    @JoinColumn(name = "product_id")
    private List<ProductItem> productItems = new ArrayList<>();

    public static Product of(Long sellerId, AddProductForm form){
        return Product.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .description(form.getDescription())
                .productItems(form.getItems().stream()
                        .map(piFrom -> ProductItem.of(sellerId, piFrom)).collect(Collectors.toList()))
                .build();

    }

    // 상품 정보 변경 (이름, 정보)
    public void updateProductInfo(Long productId, String name, String description){
        this.id = productId;
        this.name = name;
        this.description = description;
    }
}
