package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getRandomProductsByCategory(String category) {
        return productRepository.findRandomProductsByCategory(category);
    }

    public List<Product> getNewArrivalProducts() {
        return productRepository.findNewArrivalProducts();
    }
}
