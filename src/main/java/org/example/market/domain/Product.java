package org.example.market.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import static org.example.market.domain.Product.ProductStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long id;
    private String name;
    private Long price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status; // "판매중", "예약중", "완료"

    @Getter
    public enum ProductStatus {
        FOR_SALE("판매중"),
        RESERVED("예약중"),
        SOLD_OUT("완료");

        private final String description;

        ProductStatus(String description) {
            this.description = description;
        }
    }

    @ManyToOne
    @JoinColumn(name="seller_id")
    private Member seller;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int stock;

    @Builder
    public Product(String name, Long price, ProductStatus status, Member seller,int stock) {
        this.name = name;
        this.price = price;
        this.status = status;
        this.seller = seller;
        this.stock = stock;
    }

    @Builder
    public Product(Long id, String name, Long price, ProductStatus status, Member seller,int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
        this.seller = seller;
        this.stock=stock;
    }


    public void soldOut(){
        this.status= SOLD_OUT;
    }
}
