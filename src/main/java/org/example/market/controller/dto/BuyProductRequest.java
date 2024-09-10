package org.example.market.controller.dto;

import lombok.Data;

@Data
public class BuyProductRequest {
    private Long price;
    private Long quantity;
}
