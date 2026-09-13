package com.example.shop.repository;

import com.example.shop.entity.Customer;
import com.example.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);

    @Query("select o from Order o join fetch o.items i join fetch i.product where o.id = :id")
    Optional<Order> findByIdFetchItems(@Param("id") Long id);
}
