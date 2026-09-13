package com.example.shop.repository;

import com.example.shop.entity.CartItem;
import com.example.shop.entity.Customer;
import com.example.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from CartItem ci join fetch ci.product where ci.customer = :customer")
    List<CartItem> findByCustomerFetchProduct(@Param("customer") Customer customer);

    Optional<CartItem> findByCustomerAndProduct(Customer customer, Product product);

    void deleteByCustomer(Customer customer);
}
