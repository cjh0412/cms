package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.product.AddProductForm;
import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import com.cjhdev.cms.order.domain.product.UpdateProductForm;
import com.cjhdev.cms.order.domain.product.UpdateProductItemForm;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
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
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void addProduct(){
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

    @Test
    void updateProduct(){
        Long sellerId = 1L;

        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 5);
        Product product = productService.addProduct(sellerId, form);
        productRepository.findWithProductItemsById(product.getId()).get();

        UpdateProductForm updateForm = updateProductForm(product.getId(),"상품명변경" , "상품상세정보변경", product);
        Product updateProduct = productService.updateProduct(sellerId, updateForm);

        assertNotNull(updateProduct);
        assertEquals(updateProduct.getName(), "상품명변경");
        assertEquals(updateProduct.getDescription(), "상품상세정보변경");
        assertEquals(updateProduct.getProductItems().size(), 5);
        assertEquals(updateProduct.getProductItems().get(0).getName(), "상품명변경0");
        assertEquals(updateProduct.getProductItems().get(0).getPrice(), 10000);
        assertEquals(updateProduct.getProductItems().get(0).getCount(), 3);
    }

    @Test
    void deleteProduct(){
        Long sellerId = 1L;

        AddProductForm form = makeAddProductForm("상품명" , "상품상세정보", 5);
        Product product = productService.addProduct(sellerId, form);

        productService.deleteProduct(sellerId, product.getId());

        assertEquals(productRepository.findBySellerIdAndId(sellerId, product.getId()), Optional.empty());

    }


    // 상품 수정
    private static UpdateProductForm updateProductForm(Long productId, String name, String description, Product product){

        List<UpdateProductItemForm> itemFormList = new ArrayList<>();
        for(int i = 0; i < product.getProductItems().size(); i++){
            itemFormList.add(updateProductItemForm(productId, name+i, product.getProductItems().get(i).getId())); // 생성시 productId는 null
        }

        return UpdateProductForm.builder()
                .id(productId)
                .name(name)
                .description(description)
                .items(itemFormList)
                .build();
    }


    // 상품 옵션 변경
    private static final UpdateProductItemForm updateProductItemForm(Long productId, String name, Long itemId) {
        return UpdateProductItemForm.builder()
                .id(itemId)
                .productId(productId)
                .name(name)
                .price(10000)
                .count(3)
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