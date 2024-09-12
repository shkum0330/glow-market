package org.example.market.service;

import lombok.RequiredArgsConstructor;
import org.example.market.domain.Member;
import org.example.market.domain.Orders;
import org.example.market.domain.Product;
import org.example.market.exception.OrderNotFoundException;
import org.example.market.exception.ProductNotFoundException;
import org.example.market.exception.UnauthorizedException;
import org.example.market.repository.OrderRepository;
import org.example.market.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Orders findById(Long id) {
        return orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException("존재하지 않는 거래입니다."));
    }

    @Transactional
    public void reserveProduct(Long productId, Member buyer, Long price, Long quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 제품입니다."));

        if (product.getSeller().equals(buyer)) {
            throw new UnauthorizedException("판매자가 본인의 제품을 구매할 수 없습니다.");
        }

        if (product.getStatus() != Product.ProductStatus.FOR_SALE) {
            throw new IllegalStateException("구매할 수 없는 상태입니다.");
        }

        if (!Objects.equals(product.getPrice()*quantity, price*quantity)) {
            throw new IllegalArgumentException("제시한 가격이 일치하지 않습니다.");
        }

        orderRepository.save(new Orders(product, buyer, Orders.OrderStatus.RESERVED,quantity,price*quantity));
    }

    @Transactional
    public void approveSale(Orders orders, Member seller) {
        Product product = productRepository.findById(orders.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        if (!product.getSeller().equals(seller)) {
            throw new UnauthorizedException("판매자가 아닙니다.");
        }

        if (product.getStatus() != Product.ProductStatus.RESERVED) {
            throw new IllegalStateException("판매를 승인할 수 없는 상태입니다.");
        }

        if(orders.getQuantity() == product.getStock()) product.soldOut();

        orders.setCompleted();
    }
}