package org.example.market.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import static org.example.market.domain.Orders.OrderStatus.COMPLETED;
import static org.example.market.domain.Orders.OrderStatus.RESERVED;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name="buyer_id")
    private Member buyer;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Getter
    public enum OrderStatus {
        RESERVED("예약중"),
        COMPLETED("완료"),
        CANCELED("취소");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }
    }

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long totalPrice;

    public Orders(Product product, Member buyer, OrderStatus status, Long quantity, Long totalPrice) {
        this.product = product;
        this.buyer = buyer;
        this.status = status;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public void setCompleted(){
        this.status= COMPLETED;
    }
}
