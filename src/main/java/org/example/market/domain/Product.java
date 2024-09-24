package org.example.market.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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
    private ProductStatus status;
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
    public Product(String name, Long price, ProductStatus status, Member seller, int stock) {
        this.name = name;
        this.price = price;
        this.status = status;
        this.seller = seller;
        this.stock = stock;
    }
    public void updateDetails(String name, Long price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    public void soldOut() {
        this.status = ProductStatus.SOLD_OUT;
    }
    public void minusStock(Long stock) {
        this.stock -= stock;
    }
}