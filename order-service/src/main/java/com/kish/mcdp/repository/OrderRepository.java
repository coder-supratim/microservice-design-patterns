package com.kish.mcdp.repository;

import com.kish.mcdp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(String customerId);

    List<Order> findByStatus(String status);
}

