package com.cjhdev.cms.order.domain.repository;

import com.cjhdev.cms.order.domain.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchByName(String name);
}
