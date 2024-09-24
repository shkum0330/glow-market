package org.example.market.controller.dto;

import lombok.Data;
import org.example.market.domain.Member;
import org.example.market.domain.Product;

@Data
public class ProductRegisterRequest {
    private String name;
    private Long price;
    private Member seller;
    private Product.ProductStatus status;
    private Long stock;

    public Product toEntity(){
        return Product.builder().name(name).price(price).seller(seller).status(status).stock(stock.intValue()).build();
    }
}
