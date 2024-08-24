package com.cjhdev.cms.order.service;

import com.cjhdev.cms.order.client.RedisClient;
import com.cjhdev.cms.order.domain.product.AddProductCartFrom;
import com.cjhdev.cms.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final RedisClient redisClient;

    public Cart getCart(Long customerId){
        Cart cart = redisClient.get(customerId, Cart.class);
        return cart != null? cart : new Cart();

    }

    public Cart putCart(Long customerId, Cart cart){
        redisClient.put(customerId, cart);
        return cart;
    }

    public Cart addCart(Long custmerId, AddProductCartFrom form){
        Cart cart = redisClient.get(custmerId, Cart.class);
        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(custmerId);
        }

        // 장바구니안에 동일 상품이 있는지 확인
        Optional<Cart.Product> productOptional = cart.getProducts().stream()
                .filter(product -> product.getId().equals(form.getId()))
                .findFirst();

        //존재할 경우
        if(productOptional.isPresent()){
            Cart.Product redisProduct = productOptional.get();
            //요청한 상품 아이템
            List<Cart.ProductItem> items = form.getItems().stream().map(Cart.ProductItem::from).collect(Collectors.toList());
            Map<Long,  Cart.ProductItem> redisItemMap = redisProduct.getItems().stream()
                    .collect(Collectors.toMap(it -> it.getId(), it-> it));

            // 같은 상품의 같은 아이템 여부 체크
            if(!redisProduct.getName().equals(form.getName())){
                 cart.addMessage(redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }
            for (Cart.ProductItem item : items) {
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                // 같은 상품 다른 옵션인 경우
                if(redisItem == null){
                    // 장바구니 추가
                    redisProduct.getItems().add(item);
                }else{
                    // 같은 아이템인 경우 수량 추가
                    if(redisItem.getPrice().equals(item.getPrice())){
                        cart.addMessage(redisProduct.getName() + "의 가격 변경되었습니다. 확인 부탁드립니다.");
                    }
                    redisItem.setCount(redisItem.getCount() + item.getCount());
                }
            }

        }//존재하지 않을 경우
        else {
            Cart.Product product = Cart.Product.from(form);
            cart.getProducts().add(product);
        }

        redisClient.put(custmerId, cart);
        return cart;
    }
}
