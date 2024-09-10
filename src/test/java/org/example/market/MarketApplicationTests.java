package org.example.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.market.controller.dto.ProductRegisterRequest;
import org.example.market.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@SpringBootTest
@Transactional
class MarketApplicationTests {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private String sellerToken;
    private String buyerToken;
    private Long productId;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).apply(springSecurity()).build();

        // 회원 등록
        String seller = "{ \"username\": \"testSeller\", \"password\": \"password\" }";
        String buyer = "{ \"username\": \"testBuyer\", \"password\": \"password\" }";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(seller))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buyer))
                .andExpect(status().isOk());

        // 로그인
        sellerToken = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(seller))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replace("{\"jwt\":\"", "")
                .replace("\"}", "");

        buyerToken = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buyer))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replace("{\"jwt\":\"", "")
                .replace("\"}", "");

        // 제품 등록
        ProductRegisterRequest productRegisterRequest = new ProductRegisterRequest();
        productRegisterRequest.setName("Test Product");
        productRegisterRequest.setPrice(10000L);
        productRegisterRequest.setQuantity(100L);

        String productJson = objectMapper.writeValueAsString(productRegisterRequest);

        String response = mockMvc.perform(post("/product/add")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        productId = Long.parseLong(response.split(",")[0].split(":")[1]);
    }

    @Test
    public void test() throws Exception {
        // 비회원 제품 목록 조회
        mockMvc.perform(get("/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(10000))
                .andExpect(jsonPath("$[0].status").value("FOR_SALE"))
                .andExpect(jsonPath("$[0].stock").value(100));

        // 비회원 제품 상세 조회
        mockMvc.perform(get("/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.status").value("FOR_SALE"));

        // 구매자가 제품 구매
//        mockMvc.perform(post("/product/{id}/buy", productId)
//                        .header("Authorization", "Bearer " + buyerToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("RESERVED"));

//        // 판매자가 제품 거래 완료
//        mockMvc.perform(post("/product/{id}/approve", productId)
//                        .header("Authorization", "Bearer " + sellerToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("SOLD_OUT"));
//
//        // 판매자 구매/예약 목록 조회
//        mockMvc.perform(get("/product/seller")
//                        .header("Authorization", "Bearer " + sellerToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
//
//        // 구매자 구매/예약 목록 조회
//        mockMvc.perform(get("/product/buyer")
//                        .header("Authorization", "Bearer " + buyerToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

}
