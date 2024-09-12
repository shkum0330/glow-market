package org.example.market.controller.dto;

import lombok.Data;
import org.example.market.domain.Product;

import static org.example.market.domain.Product.*;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Long price;
    private ProductStatus status;
    private int stock;

    public ProductResponse(Product product) {

    }
}
