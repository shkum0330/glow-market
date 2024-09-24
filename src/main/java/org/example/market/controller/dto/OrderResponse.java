package org.example.market.controller.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.market.domain.Member;
import org.example.market.domain.Orders;
import org.example.market.domain.Orders.OrderStatus;
import org.example.market.domain.Product;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String productName;
    private Long price;
    private Orders.OrderStatus status;
    private Long quantity;
}
