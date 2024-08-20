package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.product.AddProductForm;
import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_PRODUCT_TEST(){
        Long sellerId = 1L;

        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 5);

        Product product = productService.addProduct(sellerId, form);

        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        assertNotNull(result);
        assertEquals(result.getName(), "상품명");
        assertEquals(result.getDescription(), "상품상세정보");
        assertEquals(result.getProductItems().size(), 5);
        assertEquals(result.getProductItems().get(0).getName(), "상품명0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
        assertEquals(result.getProductItems().get(0).getCount(), 3);


    }

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

    private static final AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(3)
                .build();

    }
}