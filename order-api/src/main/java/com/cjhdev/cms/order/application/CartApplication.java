package com.cjhdev.cms.order.application;

import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.model.ProductItem;
import com.cjhdev.cms.order.domain.product.AddProductCardFrom;
import com.cjhdev.cms.order.domain.redis.Cart;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.exception.ErrorCode;
import com.cjhdev.cms.order.service.CartService;
import com.cjhdev.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCardFrom form) {
        Product product = productSearchService.getByProductId(form.getId());
        if(product == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);

        if(cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
        }
        return cartService.addCart(customerId,form);
    }

    private boolean addAble(Cart cart, Product product, AddProductCardFrom form) {
       Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
               .findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount()+cartCount > currentCount;

                });
    }
}
