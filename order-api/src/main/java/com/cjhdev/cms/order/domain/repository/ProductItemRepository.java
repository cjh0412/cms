package com.cjhdev.cms.order.domain.repository;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
}
