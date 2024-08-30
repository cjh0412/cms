package com.cjhdev.cms.order.domain.model;

import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)

//@Setter
public class ProductItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    @Audited
    private String name;

    // 상품 가격
    @Audited
    private Integer price;

    //재고
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public static ProductItem of(Long sellerId, AddProductItemForm form) {
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
    }

    // 상품 옵션 정보 변경 (이름, 가격, 수량)
    public void updateItemProductInfo(Long itemId, String name, Integer price, Integer count){
        this.id = itemId;
        this.name = name;
        this.price = price;
        this.count = count;
    }


}
