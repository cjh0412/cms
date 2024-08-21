package com.cjhdev.cms.order.controller;

import com.cjhdev.cms.order.application.CartApplication;
import com.cjhdev.cms.order.domain.product.AddProductCardFrom;
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

    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name="X-AUTH-TOKEN") String token,
            @RequestBody AddProductCardFrom form){
        return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));
    }


}
