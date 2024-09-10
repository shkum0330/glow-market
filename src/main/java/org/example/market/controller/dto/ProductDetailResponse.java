package org.example.market.controller.dto;

import lombok.Data;
import org.example.market.domain.Product;

@Data
public class ProductDetailResponse {
    private Long id;
    private String name;
    private Long price;
    private Long sellerId;
    private String sellerName;
    private Product.ProductStatus status;
    private int stock;

    public ProductDetailResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.sellerId = product.getSeller().getId();
        this.sellerName=product.getSeller().getUsername();
        this.status = product.getStatus();
        this.stock = product.getStock();
    }

}
