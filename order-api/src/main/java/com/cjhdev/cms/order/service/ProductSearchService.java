package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.repository.ProductRepository;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;

    // 상품검색
    public List<Product> searchByName(String name) {
        return productRepository.searchByName(name);
    }

    // 상품조회
    public Product getByProductId(Long productId) {
        return productRepository.findWithProductItemsById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    //목록조회
    public List<Product> getListByProductIds(List<Long> productIds) {
        return productRepository.findAllByIdIn(productIds);
    }
}
