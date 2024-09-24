package org.example.market.repository;

import org.example.market.domain.Member;
import org.example.market.domain.Orders;
import org.example.market.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByBuyer(Member buyer);
    List<Orders> findByProduct(Product product);
}
