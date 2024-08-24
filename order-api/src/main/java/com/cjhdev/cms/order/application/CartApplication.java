package com.cjhdev.cms.order.application;

import com.cjhdev.cms.order.client.RedisClient;
import com.cjhdev.cms.order.domain.model.Product;
import com.cjhdev.cms.order.domain.model.ProductItem;
import com.cjhdev.cms.order.domain.product.AddProductCartFrom;
import com.cjhdev.cms.order.domain.redis.Cart;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.exception.ErrorCode;
import com.cjhdev.cms.order.service.CartService;
import com.cjhdev.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;
    private final RedisClient redisClient;


    // 장바구니 변경 알림 필요 상황
    // 1. 장바구니에 상품을 추가한다
    // 2. 상품의 가격 또는 수량이 변동된다
    public Cart getCart(Long customerId){
        Cart cart = refreshCart(cartService.getCart(customerId));
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());

        // 메세지 없는 것
        cartService.putCart(customerId, cart);

        return returnCart;
        
        // 2. 메세지를 보고 난 다음에, 이미 본 메세지는 스팸이 되기 때문에 제거
    }

    public void clearCart(Long customerId){
        cartService.putCart(customerId, null);
    }

    public Cart updateCart(Long customerId, Cart cart){
        // 실질적으로 변하는 데이터
        // 상품의 삭제, 수량의 변경
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }


    
    private Cart refreshCart(Cart cart){
        // 1. 상품이나 가격, 수량의 변경되었는 지 체크
        // 상황에 맞는 알람 제공
        // 2. 상품의 수량, 가격을 우리가 임의로 변경한다.
        Map<Long, Product> productMap = productSearchService.getListByProductIds(cart.getProducts().stream()
                        .map(Cart.Product::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Product::getId, product -> product));

        for(int i =0; i< cart.getProducts().size(); i++){

            Cart.Product cartProduct = cart.getProducts().get(i);
            Product p = productMap.get(cartProduct.getId());

            if(p == null){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            List<String> tmpMessages = new ArrayList<>();

            for(int j =0; j<cartProduct.getItems().size(); j++){
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if(pi == null){
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tmpMessages.add(cartProductItem.getName() + "옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChange = false, isCountNotEnough = false;
                if(!cartProductItem.getPrice().equals(pi.getPrice())){
                    isPriceChange = true;

                    cartProductItem.setPrice(pi.getPrice());

                }

                if(cartProductItem.getCount() > pi.getCount()){
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }

                if(isPriceChange && isCountNotEnough){
                    tmpMessages.add(cartProductItem.getName() + "가격변동 및 재고 부족으로 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChange) {
                    tmpMessages.add(cartProductItem.getName() + "가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    tmpMessages.add(cartProductItem.getName() + "수량이 변동되었습니다.");
                }

            }

            if(cartProduct.getItems().size() == 0){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;

            }else if(tmpMessages.size() > 0){
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + " 상품의 변동 사항 : ");
                for(String message : tmpMessages){
                    builder.append(message);
                    builder.append("\n");
                }

                cart.addMessage(builder.toString());
            }
        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;
    }

    public Cart addCart(Long customerId, AddProductCartFrom form) {
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

    private boolean addAble(Cart cart, Product product, AddProductCartFrom form) {
       Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
               .findFirst().orElse(Cart.Product.builder().id(product.getId())
                       .items(Collections.emptyList())
                       .build());

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    if(cartCount == null){
                        cartCount = 0;
                    }
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount()+cartCount > currentCount;

                });
    }
}
