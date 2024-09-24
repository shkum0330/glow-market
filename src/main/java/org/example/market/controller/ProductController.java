package org.example.market.controller;

import lombok.RequiredArgsConstructor;
import org.example.market.controller.dto.ProductDetailResponse;
import org.example.market.controller.dto.ProductRegisterRequest;
import org.example.market.controller.dto.ProductUpdateRequest;
import org.example.market.domain.Member;
import org.example.market.domain.Product;
import org.example.market.exception.UnauthorizedException;
import org.example.market.repository.OrderRepository;
import org.example.market.service.MemberService;
import org.example.market.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final MemberService memberService;
    private final OrderRepository orderRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductRegisterRequest productRegisterRequest, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Member member = memberService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        if (member.getRole() != Member.Role.SELLER) {
            throw new UnauthorizedException("판매자만 상품을 등록할 수 있습니다.");
        }
        productRegisterRequest.setSeller(member);
        productRegisterRequest.setStatus(Product.ProductStatus.FOR_SALE);
        return ResponseEntity.ok(productService.save(productRegisterRequest.toEntity()));
    }

    @GetMapping("/all") // 제품 목록 표시, stock이 0이면 품절이라 표시
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(new ProductDetailResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/seller/products")
    public ResponseEntity<List<ProductDetailResponse>> getSellerProducts(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Member seller = memberService.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        List<Product> products = productService.findBySeller(seller);
        List<ProductDetailResponse> responses = products.stream()
                .map(ProductDetailResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody ProductUpdateRequest updateRequest, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Member seller = memberService.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        Product updatedProduct = productService.updateProduct(id, updateRequest, seller);
        return ResponseEntity.ok(new ProductDetailResponse(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Member seller = memberService.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        productService.deleteProduct(id, seller);
        return ResponseEntity.ok().build();
    }
}