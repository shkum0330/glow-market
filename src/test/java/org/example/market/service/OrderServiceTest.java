package org.example.market.service;

import org.aspectj.weaver.ast.Or;
import org.example.market.domain.Member;
import org.example.market.domain.Orders;
import org.example.market.domain.Product;
import org.example.market.exception.InsufficientStockException;
import org.example.market.exception.UnauthorizedException;
import org.example.market.repository.OrderRepository;
import org.example.market.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.example.market.domain.Product.ProductStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private Member buyer;

    @Mock
    private Member seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("예약 성공")
    void reserveProductTest() {
        // given
        Product product = mock(Product.class);  // Product 객체를 mock으로 생성
        when(buyer.getId()).thenReturn(2L);  // Mock buyer ID
        when(seller.getId()).thenReturn(1L); // Mock seller ID
        when(product.getSeller()).thenReturn(seller);  // Product의 getSeller() 호출 시 seller를 반환하도록 설정
        when(product.getStock()).thenReturn(10);  // 재고 설정
        when(product.getStatus()).thenReturn(FOR_SALE);  // 제품 상태 설정
        when(product.getPrice()).thenReturn(100L);  // 제품 가격 설정

        // when
        orderService.reserveProduct(product, buyer, 100L, 1L);  // 가격과 수량 일치하게 전달

        // then
        verify(orderRepository, times(1)).save(any(Orders.class));
    }

    @Test
    @DisplayName("예약 실패 - 인증되지 않은 사용자")
    void reserveProductUnauthorizedTest() {
        // given
        Product product = new Product("Test Product", 100L, FOR_SALE, buyer, 10);
        when(buyer.getId()).thenReturn(1L);  // Mock buyer is also the seller

        // when & then
        assertThrows(UnauthorizedException.class, () -> orderService.reserveProduct(product, buyer, 100L, 1L));
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    @DisplayName("판매 승인 성공")
    void approveSaleTest() {
        // given
        Product product = mock(Product.class);
        when(product.getSeller()).thenReturn(seller);
        when(product.getStock()).thenReturn(10);
        when(product.getStatus()).thenReturn(Product.ProductStatus.FOR_SALE);
        when(product.getId()).thenReturn(1L);

        Orders order = mock(Orders.class);
        when(order.getProduct()).thenReturn(product);
        when(order.getQuantity()).thenReturn(5L); // 주문 수량이 5라고 가정
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        orderService.approveSale(order, seller);

        // then
        verify(productRepository, times(1)).findById(product.getId());
        verify(product, times(1)).minusStock(5L);
        verify(order, times(1)).setCompleted();
        verify(product, never()).soldOut();  // 재고가 남아있기 때문에 soldOut()은 호출되지 않음
    }

    @Test
    @DisplayName("판매 승인 실패 - 재고 부족")
    void approveSaleInsufficientStockTest() {
        // given
        Product product = new Product("Test Product", 100L, FOR_SALE, seller, 0);
        Orders order = new Orders(product, buyer, Orders.OrderStatus.RESERVED, 1L, 100L);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when & then
        assertThrows(InsufficientStockException.class, () -> orderService.approveSale(order, seller));
    }

    @Test
    @DisplayName("구매자 - 주문 조회 성공")
    void getOrdersByMemberTest() {
        // given
        Member member = mock(Member.class);
        Orders order1 = mock(Orders.class);
        Orders order2 = mock(Orders.class);

        when(order1.getStatus()).thenReturn(Orders.OrderStatus.RESERVED);
        when(order2.getStatus()).thenReturn(Orders.OrderStatus.COMPLETED);

        when(orderRepository.findByBuyer(member)).thenReturn(List.of(order1, order2));

        // when
        var orderResponses = orderService.getOrdersByMember(member);

        // then
        assertEquals(2, orderResponses.size());
        verify(orderRepository, times(1)).findByBuyer(member);
    }

    @Test
    @DisplayName("판매자 - 제품으로 주문 조회 성공")
    void getOrdersByProductTest() {
        // given
        Product product = mock(Product.class);
        Orders order1 = mock(Orders.class);
        Orders order2 = mock(Orders.class);
        when(orderRepository.findByProduct(product)).thenReturn(List.of(order1,order2));

        // when
        var orderResponses = orderService.getOrdersByProduct(product);

        // then
        assertEquals(2, orderResponses.size());
        verify(orderRepository, times(1)).findByProduct(product);
    }
}