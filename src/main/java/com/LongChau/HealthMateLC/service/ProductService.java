package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.ProductDTO;
import com.LongChau.HealthMateLC.model.Inventory;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.repository.InventoryRepository;
import com.LongChau.HealthMateLC.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // Get paginated products with Pageable
    public Page<Product> getProductsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findAll(pageable);
    }

    // Get total product count
    public long getTotalProductCount() {
        return productRepository.count();
    }

    // Search products with pagination using Pageable
    public Page<Product> searchProductsPaginated(String keyword, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        
        switch (type.toLowerCase()) {
            case "name":
                return productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
            case "type":
                return productRepository.findByProductTypeContainingIgnoreCase(keyword, pageable);
            case "description":
                return productRepository.findByDescriptionContainingIgnoreCase(keyword, pageable);
            case "all":
            default:
                return productRepository.findByProductNameContainingIgnoreCaseOrProductTypeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword, keyword, keyword, pageable);
        }
    }

    // Get search result count
    public long getSearchProductCount(String keyword, String type) {
        Page<Product> result = searchProductsPaginated(keyword, type, 0, Integer.MAX_VALUE);
        return result.getTotalElements();
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Find by ID
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    public Optional<Product> findById(Integer productId) {
        return productRepository.findById(productId);
    }

    public Optional<Product> getProductByName(String productName) {
        return productRepository.findByProductName(productName);
    }

    // Search without pagination (for backward compatibility)
    public List<Product> searchProducts(String keyword, String type) {
        switch (type.toLowerCase()) {
            case "name":
                return productRepository.findByProductNameContainingIgnoreCase(keyword);
            case "type":
                return productRepository.findByProductTypeContainingIgnoreCase(keyword);
            case "description":
                return productRepository.findByDescriptionContainingIgnoreCase(keyword);
            case "all":
            default:
                return productRepository.findByProductNameContainingIgnoreCaseOrProductTypeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword, keyword, keyword);
        }
    }

    // Get products by type
    public Page<Product> getProductsByTypePaginated(String productType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findByProductTypeContainingIgnoreCase(productType, pageable);
    }

    public List<Product> getProductsByType(String productType) {
        return productRepository.findByProductTypeContainingIgnoreCase(productType);
    }

    // Get products by price range
    public Page<Product> getProductsByPriceRangePaginated(double minPrice, double maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // Get products with price greater than
    public Page<Product> getProductsByPriceGreaterThanPaginated(double price, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findByPriceGreaterThan(price, pageable);
    }

    public List<Product> getProductsByPriceGreaterThan(double price) {
        return productRepository.findByPriceGreaterThan(price);
    }

    // Get products with price less than
    public Page<Product> getProductsByPriceLessThanPaginated(double price, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findByPriceLessThan(price, pageable);
    }

    public List<Product> getProductsByPriceLessThan(double price) {
        return productRepository.findByPriceLessThan(price);
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
        return productRepository.findByProductNameContainingIgnoreCase(productName);
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
