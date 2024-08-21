package com.cjhdev.cms.order.controller;

import com.cjhdev.cms.order.domain.product.ProductDto;
import com.cjhdev.cms.order.service.ProductSearchService;
import com.cjhdev.cms.order.service.ProductService;
import config.JwtAuthenticationProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search/product")
@RequiredArgsConstructor
public class SearchController {
    private final ProductSearchService productSearchService;


    @GetMapping
    public ResponseEntity<List<ProductDto>> searchProduct(@RequestParam("name") String name) {
        return ResponseEntity.ok(productSearchService.searchByName(name).stream()
                .map(ProductDto::withoutItemsFrom).collect(Collectors.toList()));
    }

    @GetMapping("/detail")
    public ResponseEntity<ProductDto> getDetail (@RequestParam("productId") Long productId) {
        return ResponseEntity.ok(ProductDto.from(productSearchService.getByProductId(productId)));
    }


}
