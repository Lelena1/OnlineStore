package com.example.shop.seed;

import com.example.shop.entity.Product;
import com.example.shop.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public ProductSeeder(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    private record SeedProduct(String name, String description, BigDecimal price, String category, int stock,
                                String imageUrl) {
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            return;
        }

        try (InputStream in = new ClassPathResource("data/products.json").getInputStream()) {
            List<SeedProduct> seedProducts = objectMapper.readValue(in, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, SeedProduct.class));

            List<Product> products = seedProducts.stream().map(sp -> {
                Product product = new Product();
                product.setName(sp.name());
                product.setDescription(sp.description());
                product.setPrice(sp.price());
                product.setCategory(sp.category());
                product.setStock(sp.stock());
                product.setImageUrl(sp.imageUrl());
                return product;
            }).toList();

            productRepository.saveAll(products);
        }
    }
}
