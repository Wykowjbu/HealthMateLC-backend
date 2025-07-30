package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.ProductDTO;
import com.LongChau.HealthMateLC.model.Inventory;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.repository.InventoryRepository;
import com.LongChau.HealthMateLC.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private FileUploadService fileUploadService;

    public List<Product> getProductsPaginated(int offset, int size) {
        Pageable pageable = PageRequest.of(offset, size);
        return productRepository.findAll(pageable).getContent();
    }

    public long getTotalProductCount() {
        return productRepository.count();
    }

    public List<Product> searchProductsPaginated(String keyword, String type, int offset, int size) {
        return productRepository.searchProductsPaginated(keyword, type, offset, size);
    }

    public long getSearchProductCount(String keyword, String type) {
        return productRepository.getSearchProductCount(keyword, type);
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

    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        // Validate duplicate product name
        if (productRepository.existsByProductName(productDTO.getProductName())) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại");
        }
        
        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setProductType(productDTO.getProductType());
        product.setUnit(productDTO.getUnit());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        
        // Xử lý ảnh
        if (productDTO.getImageBase64() != null && !productDTO.getImageBase64().isEmpty()) {
            try {
                String imageUrl = fileUploadService.convertBase64ToFile(
                    productDTO.getImageBase64(), 
                    productDTO.getProductName() + ".jpg"
                );
                product.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh sản phẩm", e);
            }
        }
        
        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setNumber(productDTO.getQuantity() != null ? productDTO.getQuantity() : 0);
        inventoryRepository.save(inventory);
        
        return savedProduct;
    }
    

    public Product updateProduct(Integer productId, ProductDTO productDTO) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
        
        Product product = existingProduct.get();
        
        // Check if name is changed and if new name already exists
        if (!product.getProductName().equals(productDTO.getProductName()) && 
            productRepository.existsByProductName(productDTO.getProductName())) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại");
        }
        
        // Xóa ảnh cũ nếu có ảnh mới
        if (productDTO.getImageBase64() != null && !productDTO.getImageBase64().isEmpty()) {
            // Xóa ảnh cũ
            if (product.getImageUrl() != null) {
                fileUploadService.deleteImage(product.getImageUrl());
            }
            
            // Lưu ảnh mới
            try {
                String imageUrl = fileUploadService.convertBase64ToFile(
                    productDTO.getImageBase64(), 
                    productDTO.getProductName() + ".jpg"
                );
                product.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh sản phẩm", e);
            }
        }
        
        product.setProductName(productDTO.getProductName());
        product.setProductType(productDTO.getProductType());
        product.setUnit(productDTO.getUnit());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        
        return productRepository.save(product);
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
    
    /**
     * Lấy số lượng tồn kho của sản phẩm
     */
    public Integer getProductQuantity(Integer productId) {
        return inventoryRepository.findById(productId)
            .map(Inventory::getNumber)
            .orElse(0);
    }
}
