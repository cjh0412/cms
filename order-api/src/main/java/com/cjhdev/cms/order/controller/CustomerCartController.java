package com.cjhdev.cms.order.controller;

import com.cjhdev.cms.order.OrderApplication;
import com.cjhdev.cms.order.application.CartApplication;
import com.cjhdev.cms.order.application.OrderAPIApplication;
import com.cjhdev.cms.order.domain.product.AddProductCartFrom;
import com.cjhdev.cms.order.domain.redis.Cart;
import config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {


    private final CartApplication cartApplication;
    private final JwtAuthenticationProvider provider;
    private final OrderAPIApplication orderApplication;

    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name="X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartFrom form){
        return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));
    }


    @GetMapping
    public ResponseEntity<Cart> showCart(
            @RequestHeader(name="X-AUTH-TOKEN") String token){
            return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
    }

    @PutMapping
    public ResponseEntity<Cart> updateCart(
            @RequestHeader(name="X-AUTH-TOKEN") String token,
            @RequestBody Cart cart
    ){
        return ResponseEntity.ok(cartApplication.updateCart(provider.getUserVo(token).getId() , cart));
    }

    @PostMapping("/order")
    public ResponseEntity<Cart> order(
            @RequestHeader(name="X-AUTH-TOKEN") String token,
            @RequestBody Cart cart
    ){
        orderApplication.cartOrder(token, cart);
        return ResponseEntity.ok().build();
    }


}
