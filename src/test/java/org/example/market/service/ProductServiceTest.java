package org.example.market.service;

import org.example.market.controller.dto.ProductUpdateRequest;
import org.example.market.domain.Member;
import org.example.market.domain.Product;
import org.example.market.exception.UnauthorizedException;
import org.example.market.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.example.market.domain.Product.ProductStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Mock
    private Member seller;  // Member 객체를 mock으로 생성

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("제품 등록 성공")
    void saveProductTest() {
        // given
        Product product = new Product("Test Product", 100L, FOR_SALE, seller, 10);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Product savedProduct = productService.save(product);

        // then
        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("제품 id로 검색 성공")
    void findByIdProductTest() {
        // given
        Long productId = 1L;
        Product product = new Product("Test Product", 100L, FOR_SALE, seller, 10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        Optional<Product> foundProduct = productService.findById(productId);

        // then
        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("제품 id로 검색 실패 - 존재하지 않는 제품")
    void findByIdProductNotFoundTest() {
        // given
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when
        Optional<Product> foundProduct = productService.findById(productId);

        // then
        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("제품 수정 성공")
    void updateProductTest() {
        // given
        Long productId = 1L;
        Product existingProduct = new Product("Old Product", 100L, FOR_SALE, seller, 10);
        ProductUpdateRequest updateRequest = new ProductUpdateRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setPrice(200L);
        updateRequest.setStock(20);
        updateRequest.setStatus(RESERVED);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when
        Product updatedProduct = productService.updateProduct(productId, updateRequest, seller);

        // then
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(200L, updatedProduct.getPrice());
        assertEquals(20, updatedProduct.getStock());
        assertEquals(RESERVED, updatedProduct.getStatus());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("다른 판매자가 제품 수정 시도")
    void updateProductUnauthorizedTest() {
        // given
        Long productId = 1L;
        Member otherSeller = mock(Member.class);  // 다른 판매자 mock 생성
        Product existingProduct = new Product("Old Product", 100L, FOR_SALE, seller, 10);
        ProductUpdateRequest updateRequest = new ProductUpdateRequest();
        updateRequest.setName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when & then
        assertThrows(UnauthorizedException.class, () -> productService.updateProduct(productId, updateRequest, otherSeller));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("제품 삭제 성공")
    void deleteProductTest() {
        // given
        Long productId = 1L;
        Product existingProduct = new Product("Test Product", 100L,FOR_SALE, seller, 10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when
        productService.deleteProduct(productId, seller);

        // then
        verify(productRepository, times(1)).delete(existingProduct);
    }

    @Test
    @DisplayName("인가되지 않은 판매자가 제품 등록 시도")
    void deleteProductUnauthorizedTest() {
        // given
        Long productId = 1L;
        Member otherSeller = mock(Member.class);  // 다른 판매자 mock 생성
        Product existingProduct = new Product("Test Product", 100L, FOR_SALE, seller, 10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // when & then
        assertThrows(UnauthorizedException.class, () -> productService.deleteProduct(productId, otherSeller));
        verify(productRepository, times(1)).findById(productId);
    }
}