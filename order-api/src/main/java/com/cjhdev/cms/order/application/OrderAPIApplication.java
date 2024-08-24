package com.cjhdev.cms.order.application;

import com.cjhdev.cms.order.client.UserClient;
import com.cjhdev.cms.order.client.customer.ChangeBalanceForm;
import com.cjhdev.cms.order.client.customer.CustomerDto;
import com.cjhdev.cms.order.domain.model.ProductItem;
import com.cjhdev.cms.order.domain.redis.Cart;
import com.cjhdev.cms.order.exception.CustomException;
import com.cjhdev.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static com.cjhdev.cms.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.cjhdev.cms.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

@Service
@RequiredArgsConstructor
public class OrderAPIApplication {

    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    @Transactional
    public void cartOrder (String token, Cart cart) {
        // 1. 주문 시 기존 카트 비움
        Cart orderCart = cartApplication.refreshCart(cart);
//        Cart orderCart = cartApplication.getCart(customerId);
        if(orderCart.getMessages().size() > 0) {
            // 문제 존재
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);
        if(customerDto.getBalance() < getTotalPrice(cart)){
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }

        userClient.changeBalance(token, ChangeBalanceForm.builder()
                        .from("USER")
                        .message("Order")
                        .money(-totalPrice)
                        .build());

        for(Cart.Product product: orderCart.getProducts()){
            for(Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }
    }

    private Integer getTotalPrice(Cart cart) {

        return cart.getProducts().stream().flatMapToInt(product ->
                product.getItems().stream().flatMapToInt(productItem ->
                        IntStream.of(productItem.getPrice() * productItem.getCount())))
                        .sum();
    }



    // 결제를 위해 필요한것
    // 1. 장바구니에 담은 물건이 주문 가능한 상태인지
    // 2. 가격 변동 여부 체크
    // 3. 고객의 돈이 충분한지
    // 4. 결제 & 상품의 재고 관리
}
