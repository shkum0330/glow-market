package org.example.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.market.controller.dto.ProductUpdateRequest;
import org.example.market.domain.Member;
import org.example.market.domain.Product;
import org.example.market.domain.Orders;
import org.example.market.exception.ProductNotFoundException;
import org.example.market.exception.UnauthorizedException;
import org.example.market.repository.ProductRepository;
import org.example.market.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Product save(Product product) {
        log.info("제품 등록 = {}",product);
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByStatus(Product.ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    public List<Product> findBySeller(Member seller) {
        return productRepository.findBySeller(seller);
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest updateRequest, Member seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        if (!product.getSeller().equals(seller)) {
            throw new UnauthorizedException("상품을 수정할 권한이 없습니다.");
        }

        product.updateDetails(
                updateRequest.getName(),
                updateRequest.getPrice(),
                updateRequest.getStock()
        );

        if (updateRequest.getStatus() != null) {
            product.setStatus(updateRequest.getStatus());
        }

        return product;
    }

    @Transactional
    public void deleteProduct(Long id, Member seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        if (!product.getSeller().equals(seller)) {
            throw new UnauthorizedException("상품을 삭제할 권한이 없습니다.");
        }

        productRepository.delete(product);
    }
}