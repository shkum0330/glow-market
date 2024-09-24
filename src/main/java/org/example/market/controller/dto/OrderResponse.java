package org.example.market.controller.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.market.domain.Member;
import org.example.market.domain.Orders;
import org.example.market.domain.Orders.OrderStatus;
import org.example.market.domain.Product;

@Data
public class OrderResponse {
    private Long id;
    private String productName;
    private Long price;
    private String status;
    private Long quantity;

    public OrderResponse(Long id, String productName, Long price, OrderStatus status, Long quantity) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.status = status.getDescription();
        this.quantity = quantity;
    }

    public OrderResponse(Long id, Long price, Long quantity) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
    }
}
