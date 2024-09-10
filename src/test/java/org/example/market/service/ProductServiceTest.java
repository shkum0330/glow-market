package org.example.market.service;

import org.example.market.domain.Member;
import org.example.market.domain.Product;
import org.example.market.repository.MemberRepository;
import org.example.market.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.example.market.domain.Member.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ProductServiceTest {
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private MemberRepository memberRepository;
    @Autowired
    private ProductService productService;

    private Product product1,product2;
    private Member seller;
    private Member buyer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seller = new Member(1L,"seller","1234", SELLER);
        buyer = new Member(2L,"buyer","1234", BUYER);

        product1 = new Product(1L,"Test Product1",10000L, Product.ProductStatus.FOR_SALE,seller,100);
        product2 = new Product(2L,"Test Product2",20000L, Product.ProductStatus.RESERVED,seller,1);
    }
    @Test
    @DisplayName("제품 판매 등록")
    void addProduct() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product savedProduct = productService.save(product1);

        assertNotNull(savedProduct);
        assertEquals(product1.getName(), savedProduct.getName());
        assertEquals(product1.getPrice(), savedProduct.getPrice());
        assertEquals(product1.getStatus(), savedProduct.getStatus());
        assertEquals(product1.getSeller(), savedProduct.getSeller());

        verify(productRepository, times(1)).save(product1);
    }

    @Test
    @DisplayName("모든 제품 검색")
    void findAllProducts() {
        List<Product> products = List.of(product1);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> foundProducts = productService.findAll();

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("id로 제품 검색")
    void findProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.findById(1L);

        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getName(), foundProduct.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }
    @Test
    @DisplayName("상태로 제품 검색")
    void testFindProductsByStatus() {
        List<Product> products = List.of(product1);
        when(productRepository.findByStatus(Product.ProductStatus.FOR_SALE)).thenReturn(products);

        List<Product> foundProducts = productService.findByStatus(Product.ProductStatus.FOR_SALE);

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        verify(productRepository, times(1)).findByStatus(Product.ProductStatus.FOR_SALE);
    }

    @Test
    @DisplayName("판매자로 제품 검색")
    void testFindProductsBySeller() {
        List<Product> products = List.of(product1);
        when(productRepository.findBySeller(seller)).thenReturn(products);

        List<Product> foundProducts = productService.findBySeller(seller);

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        verify(productRepository, times(1)).findBySeller(seller);
    }

    @Test
    @DisplayName("제품 예약하기")
    void reserveProduct() {

    }
//    @Test
//    @DisplayName("구매 성공")
//    public void testBuyProductSuccess() {
//        when(memberRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//        when(productRepository.save(any(Product.class))).thenReturn(product1);
//
//        BuyProductRequest buyProductRequest = new BuyProductRequest();
//        buyProductRequest.setPrice(10000L);
//
//        assertDoesNotThrow(() -> productService.buyProduct(1L, buyer, buyProductRequest.getPrice()));
//    }

}