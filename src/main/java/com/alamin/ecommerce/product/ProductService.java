package com.alamin.ecommerce.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

    public Page<Product> getAllProducts(int pageNumber, int count) {
        if (pageNumber < 1) pageNumber = 1;
        if (count < 10) count = 10;
        Pageable pageable = PageRequest.of(pageNumber, count);  // 20 products per page
        return productRepository.findAll(pageable);
    }
}
