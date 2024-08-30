package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.model.ProductItem;
import com.cjhdev.cms.order.domain.product.AddProductForm;
import com.cjhdev.cms.order.domain.product.UpdateProductForm;
import com.cjhdev.cms.order.domain.product.UpdateProductItemForm;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    // 상품 추가
    public Product addProduct(Long sellerId, AddProductForm form){
        return productRepository.save(Product.of(sellerId, form));
    }

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form){
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        //Entity setter 제거
//        product.setName(form.getName());
//        product.setDescription(form.getDescription());
        // 상품 정보 변경
        product.updateProductInfo(form.getId(), form.getName(), form.getDescription());

        for (UpdateProductItemForm itemForm : form.getItems()){
            ProductItem item = product.getProductItems().stream()
                    .filter(piForm -> piForm.getId().equals(itemForm.getId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT_ITEM));

            //            Entity setter 제거
//            item.setName(itemForm.getName());
//            item.setPrice(itemForm.getPrice());
//            item.setCount(itemForm.getCount());

            // 상품 옵션 정보 변경
            item.updateItemProductInfo(itemForm.getId(), itemForm.getName(), itemForm.getPrice(), item.getCount());
        }

        return product;

    }

    @Transactional
    public void deleteProduct(Long sellerId, Long productId){
        Product product = productRepository.findBySellerIdAndId(sellerId, productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        productRepository.delete(product);
    }

}
