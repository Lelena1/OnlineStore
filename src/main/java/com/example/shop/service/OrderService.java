package com.example.shop.service;

import com.example.shop.entity.CartItem;
import com.example.shop.entity.Customer;
import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.entity.Product;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order checkout(Customer customer) {
        List<CartItem> cartItems = cartService.getCart(customer);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        for (CartItem cartItem : cartItems) {
            if (cartItem.getQuantity() > cartItem.getProduct().getStock()) {
                throw new IllegalStateException("Недостаточно товара на складе: " + cartItem.getProduct().getName());
            }
        }

        Order order = new Order();
        order.setCustomer(customer);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setLineTotal(lineTotal);
            order.getItems().add(orderItem);

            total = total.add(lineTotal);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        order.setTotal(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(customer);
        return saved;
    }

    public List<Order> getOrderHistory(Customer customer) {
        return orderRepository.findByCustomerOrderByOrderDateDesc(customer);
    }

    public Order getOrderForCustomer(Long orderId, Customer customer) {
        Order order = orderRepository.findByIdFetchItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("Заказ принадлежит другому покупателю");
        }
        return order;
    }
}
