package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.model.ProductItem;
import com.cjhdev.cms.order.domain.product.AddProductItemForm;
import com.cjhdev.cms.order.domain.product.ProductDto;
import com.cjhdev.cms.order.domain.product.UpdateProductItemForm;
import com.cjhdev.cms.order.domain.repository.ProductItemRepository;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductItem getProductItem(Long id) {
        return productItemRepository.getById(id);
    }

    @Transactional
    // 상품에 대한 옵션만 추가
    public Product addProductItem(Long sellerId, AddProductItemForm form) {
        // 상품 존재 여부 체크
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 동일 옵션명 존재 여부 체크
        if(product.getProductItems().stream()
                .anyMatch(item -> item.getName().equals(form.getName()))) {
            throw new CustomException(ErrorCode.SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);
        return product;
    }

    @Transactional
    public ProductItem updateProductItem(Long sellerId,  UpdateProductItemForm form) {
        ProductItem productItem = productItemRepository.findById(form.getId())
                .filter(piForm -> piForm.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT_ITEM));
        
//        setter 제거
//        productItem.setName(form.getName());
//        productItem.setPrice(form.getPrice());
//        productItem.setCount(form.getCount());

        productItem.updateItemProductInfo(form.getId(), form.getName(), form.getPrice(), form.getCount());
        return productItem;
    }

    @Transactional
    public void deleteProductItem(Long sellerId, Long productItemId){
        ProductItem productItem = productItemRepository.findById(productItemId)
                .filter(piForm -> piForm.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT_ITEM));

        productItemRepository.delete(productItem);
    }
}
