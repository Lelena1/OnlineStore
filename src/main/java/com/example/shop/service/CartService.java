package com.example.shop.service;

import com.example.shop.entity.CartItem;
import com.example.shop.entity.Customer;
import com.example.shop.entity.Product;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addToCart(Customer customer, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + productId));

        CartItem item = cartItemRepository.findByCustomerAndProduct(customer, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCustomer(customer);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newQuantity = Math.min(item.getQuantity() + Math.max(quantity, 1), product.getStock());
        item.setQuantity(Math.max(newQuantity, 1));
        cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(Customer customer, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + productId));

        cartItemRepository.findByCustomerAndProduct(customer, product).ifPresent(item -> {
            if (quantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(Math.min(quantity, product.getStock()));
                cartItemRepository.save(item);
            }
        });
    }

    @Transactional
    public void removeFromCart(Customer customer, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + productId));

        cartItemRepository.findByCustomerAndProduct(customer, product).ifPresent(cartItemRepository::delete);
    }

    public List<CartItem> getCart(Customer customer) {
        return cartItemRepository.findByCustomerFetchProduct(customer);
    }

    public BigDecimal getCartTotal(Customer customer) {
        return getCart(customer).stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void clearCart(Customer customer) {
        cartItemRepository.deleteByCustomer(customer);
    }
}
