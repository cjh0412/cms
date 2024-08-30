package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.model.ProductItem;
import com.cjhdev.cms.order.domain.product.AddProductForm;
import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import com.cjhdev.cms.order.domain.product.UpdateProductForm;
import com.cjhdev.cms.order.domain.product.UpdateProductItemForm;
import com.cjhdev.cms.order.domain.repository.ProductItemRepository;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductItemServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductItemService productItemService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductItemRepository productItemRepository;

    @Test
    void getProductItem() {
        Long sellerId = 1L;
        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 1);
        productService.addProduct(sellerId, form);

        ProductItem productItem = productItemService.getProductItem(1L);

        assertNotNull(productItem);
        assertEquals("상품명0", productItem.getName());
        assertEquals(10000, productItem.getPrice());
        assertTrue(productItem.getCount().equals(3));
    }

    @Test
    void addProductItem() {
        Long sellerId = 1L;

        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 1);
        Product product = productService.addProduct(sellerId, form);

        AddProductItemForm itemForm = makeProductItemForm(product.getId(), "아이템추가");
        productItemService.addProductItem(sellerId, itemForm);

        Product result = productRepository.findWithProductItemsById(product.getId()).get();


        assertNotNull(result);
        assertEquals(result.getProductItems().size(), 2);
        assertEquals(result.getProductItems().get(1).getName(), "아이템추가");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
        assertEquals(result.getProductItems().get(0).getCount(), 3);

        AddProductItemForm itemForm2 = makeProductItemForm(product.getId(), "아이템추가");
        CustomException exception =Assertions.assertThrows(CustomException.class,()
                -> productItemService.addProductItem(sellerId, itemForm2));

        assertEquals(itemForm2.getName(), result.getProductItems().get(1).getName());
        assertEquals(ErrorCode.SAME_ITEM_NAME, exception.getErrorCode());

    }


    @Test
    void updateProductItem() {

        Long sellerId = 1L;

        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 2);
        Product product = productService.addProduct(sellerId, form);

        UpdateProductItemForm itemForm = updateProductItemForm(product.getId(), "아이템 명 변경", 12000, 4, product.getProductItems().get(1).getId());
        productItemService.updateProductItem(sellerId, itemForm);

        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        assertNotNull(result);
        assertEquals(result.getProductItems().size(), 2);
        assertEquals(result.getProductItems().get(1).getName(), "아이템 명 변경");
        assertEquals(result.getProductItems().get(1).getPrice(), 12000);
        assertEquals(result.getProductItems().get(1).getCount(), 4);

    }

    @Test
    void deleteProductItem() {

        Long sellerId = 2L;

        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 5);
        Product product = productService.addProduct(sellerId, form);
        productItemService.deleteProductItem(sellerId, product.getProductItems().get(0).getId());

        assertEquals(Optional.empty(), productItemRepository.findById(0L));
        assertNotNull(productItemRepository.findById(2L));
    }



    // 상품 옵션 변경
    private static final UpdateProductItemForm updateProductItemForm(Long productId, String name, Integer price, Integer count, Long itemId) {
        return UpdateProductItemForm.builder()
                .id(itemId)
                .productId(productId)
                .name(name)
                .price(price)
                .count(count)
                .build();

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