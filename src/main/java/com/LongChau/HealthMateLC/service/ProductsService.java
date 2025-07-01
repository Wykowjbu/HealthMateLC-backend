package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductsService(ProductRepository productRepository){this.productRepository=productRepository;}
    public List<Product> getAll(){return productRepository.findAll();}


}
