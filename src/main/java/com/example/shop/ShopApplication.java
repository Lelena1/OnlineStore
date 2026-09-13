package com.example.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ShopApplication {

    public static void main(String[] args) {
        // Must exist before the DataSource bean is created, or sqlite-jdbc fails to open the file.
        new File("data").mkdirs();
        SpringApplication.run(ShopApplication.class, args);
    }
}
