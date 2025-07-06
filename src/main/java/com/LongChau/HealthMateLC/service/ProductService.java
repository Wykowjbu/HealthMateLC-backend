package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.ProductDTO;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(ProductDTO productDTO) {

        if (productRepository.findByProductName(productDTO.getProductName().trim()).isPresent()) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại trong hệ thống");
        }
        
        // Tạo sản phẩm mới
        Product product = new Product();
        product.setProductName(productDTO.getProductName().trim());
        product.setProductType(productDTO.getProductType().trim());
        product.setUnit(productDTO.getUnit().trim());
        product.setDescription(productDTO.getDescription() != null ? productDTO.getDescription().trim() : "");
        product.setPrice(productDTO.getPrice());
        
        return productRepository.save(product);
    }
    

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    public Optional<Product> findById(Integer productId) {
        return productRepository.findById(productId);
    }
    

    public Optional<Product> getProductByName(String productName) {
        return productRepository.findByProductName(productName);
    }

    public List<Product> searchProducts(String keyword, String type) {
        return productRepository.searchProducts(keyword, type);
    }

    public Product updateProduct(Integer productId, ProductDTO productDTO) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
        
        Product product = existingProduct.get();
        
        // Kiểm tra tên mới có trùng với sản phẩm khác không
        Optional<Product> duplicateName = productRepository.findByProductName(productDTO.getProductName().trim());
        if (duplicateName.isPresent() && !duplicateName.get().getProductId().equals(productId)) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại trong hệ thống");
        }
        
        // Cập nhật thông tin
        product.setProductName(productDTO.getProductName().trim());
        product.setProductType(productDTO.getProductType().trim());
        product.setUnit(productDTO.getUnit().trim());
        product.setDescription(productDTO.getDescription() != null ? productDTO.getDescription().trim() : "");
        product.setPrice(productDTO.getPrice());
        
        return productRepository.save(product);
    }

    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
        productRepository.deleteById(productId);
    }

    public List<Product> searchProductsByName(String productName) {
        // TODO: Implement search functionality
        return productRepository.findAll();
    }
    

    public List<Product> getProductsByType(String productType) {
        // TODO: Implement filter by type
        return productRepository.findAll();
    }
    

    public long countProducts() {
        return productRepository.count();
    }
    

    public boolean existsById(Integer productId) {
        return productRepository.existsById(productId);
    }
    

    public boolean existsByName(String productName) {
        return productRepository.findByProductName(productName).isPresent();
    }
}
