package com.cjhdev.cms.order.application;

import com.cjhdev.cms.order.config.TestRedisConfig;
import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.product.AddProductCartFrom;
import com.cjhdev.cms.order.domain.product.AddProductForm;
import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import com.cjhdev.cms.order.domain.redis.Cart;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import com.cjhdev.cms.order.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class CartApplicationTest {

    @Autowired
    private CartApplication cartApplication;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_TEST_MODIFY() {
        Long customerId = 100L;
        cartApplication.clearCart(customerId);

        Product product = add_product();
        Product result = productRepository.findWithProductItemsById(product.getId()).get();
        assertNotNull(result);
        assertEquals(result.getName(), "상품명");
        assertEquals(result.getDescription(), "상품상세정보");
        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "상품명0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
        // assertEquals(result.getProductItems().get(0).getCount(), 3);

        Cart cart = cartApplication.addCart(customerId, makeForm(result));

        assertEquals(cart.getMessages().size(), 0);
        cart = cartApplication.getCart(customerId);
        assertEquals(cart.getMessages().size(), 1);


    }

    AddProductCartFrom makeForm(Product p){
        AddProductCartFrom.ProductItem productItem =
                AddProductCartFrom.ProductItem.builder()
                        .id(p.getProductItems().get(0).getId())
                        .name(p.getProductItems().get(0).getName())
                        .count(3)
                        .price(20000)
                        .build();

        return  AddProductCartFrom.builder()
                        .id(p.getId())
                        .sellerId(p.getSellerId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .items(List.of(productItem))
                        .build();
    }

    @Test
    void addCart() {
    }


    Product add_product(){
        Long sellerId = 1L;
        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 3);
        Product product = productService.addProduct(sellerId, form);
        return productService.addProduct(sellerId, form);
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
                .count(10)
                .build();

    }

}