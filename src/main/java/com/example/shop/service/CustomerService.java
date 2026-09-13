package com.example.shop.service;

import com.example.shop.entity.Customer;
import com.example.shop.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer loginOrCreate(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        return customerRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setName(name);
                    return customerRepository.save(customer);
                });
    }

    public Customer requireById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Покупатель не найден: " + id));
    }
}
