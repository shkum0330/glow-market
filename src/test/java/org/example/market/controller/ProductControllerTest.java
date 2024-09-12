package org.example.market.controller;

import org.example.market.config.SecurityConfig;
import org.example.market.domain.Member;
import org.example.market.domain.Product;
import org.example.market.jwt.JwtUtil;
import org.example.market.repository.OrderRepository;
import org.example.market.service.MemberService;
import org.example.market.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    @MockBean
    private MemberService memberService;
    @MockBean
    private OrderRepository transactionRepository;
    @MockBean
    private JwtUtil jwtUtil;

    private Product product;
    private Member seller;

    @BeforeEach
    void setUp() {
        seller = new Member(1L, "seller", "1234", Member.Role.SELLER);
        product = new Product(1L, "Test Product", 10000L, Product.ProductStatus.FOR_SALE, seller,100);
    }

    @Test
    void getProductById_Success() throws Exception {
        when(productService.findById(anyLong())).thenReturn(Optional.of(product));

        mockMvc.perform(get("/product/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10000L))
                .andExpect(jsonPath("$.status").value("FOR_SALE"))
                .andExpect(jsonPath("$.sellerId").value(1L))
                .andExpect(jsonPath("$.sellerName").value("seller"))
                .andExpect(jsonPath("$.stock").value(100));
    }

    @Test
    void getProductById_NotFound() throws Exception {
        when(productService.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/product/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}
