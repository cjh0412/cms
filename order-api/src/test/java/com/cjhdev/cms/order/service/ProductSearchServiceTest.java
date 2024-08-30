package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.product.AddProductForm;
import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductSearchServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSearchService productSearchService;

    @Test
    void searchByName() {

        Long sellerId = 3L;
        AddProductForm form = makeAddProductForm("1번 상품명" , "1번 상품상세정보", 1);
        productService.addProduct(sellerId, form);

        AddProductForm form2 = makeAddProductForm("2번 상품명" , "2번 상품상세정보", 2);
        productService.addProduct(sellerId, form2);

        List<Product> products = productSearchService.searchByName("2번");

        assertEquals(1, products.size());
        assertEquals(products.get(0).getName(), "2번 상품명");

    }

    // 상품 추가
    private static AddProductForm makeAddProductForm(String name, String description, int itemCount){
        List<AddProductItemForm> itemFormList = new ArrayList<>();
        for(int i = 0; i < itemCount; i++){
            itemFormList.add(makeProductItemForm(null, name+i)); // 생성시 productId는 null
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemFormList)
                .build();
    }

    // 상품 옵션 추가
    private static final AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(3)
                .build();

    }
}