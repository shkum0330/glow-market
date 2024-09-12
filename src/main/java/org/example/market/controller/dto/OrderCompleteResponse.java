package org.example.market.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.market.domain.Orders.OrderStatus;

@Data
@AllArgsConstructor
public class OrderCompleteResponse {
    Long id;
    Long buyerId;
    Long quantity;
    OrderStatus status;
}
