package org.example.market.controller.dto;

import lombok.Data;
import org.example.market.domain.Product;

@Data
public class ProductUpdateRequest {
    private String name;
    private Long price;
    private Product.ProductStatus status;
    private Integer stock;
}
