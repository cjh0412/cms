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
    public Product addProduct(Long sellerId, AddProductForm form){
        return productRepository.save(Product.of(sellerId, form));
    }

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form){
        Product p = productRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        p.setName(form.getName());
        p.setDescription(form.getDescription());

        for (UpdateProductItemForm itemForm : form.getItems()){
            ProductItem item = p.getProductItems().stream()
                    .filter(piForm -> piForm.getId().equals(itemForm.getId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT_ITEM));

            item.setName(itemForm.getName());
            item.setPrice(itemForm.getPrice());
            item.setCount(itemForm.getCount());
        }

        return p;

    }

}
