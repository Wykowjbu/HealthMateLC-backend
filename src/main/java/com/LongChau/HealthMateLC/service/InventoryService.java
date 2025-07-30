package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.InventoryDTO;
import com.LongChau.HealthMateLC.model.Inventory;
import com.LongChau.HealthMateLC.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Lấy số lượng tồn kho của sản phẩm
     */
    public Integer getProductQuantity(Integer productId) {
        Optional<Inventory> inventory = inventoryRepository.findById(productId);
        return inventory.map(Inventory::getNumber).orElse(0);
    }

    /**
     * Cập nhật số lượng tồn kho
     */
    @Transactional
    public boolean updateProductQuantity(InventoryDTO inventoryDTO) {
        Integer productId = inventoryDTO.getProductId();
        Integer quantity = inventoryDTO.getQuantity();
        String operation = inventoryDTO.getOperation();

        Optional<Inventory> existingInventory = inventoryRepository.findById(productId);

        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            int currentQuantity = inventory.getNumber();

            switch (operation) {
                case "add":
                    inventory.setNumber(currentQuantity + quantity);
                    break;
                case "subtract":
                    if (currentQuantity >= quantity) {
                        inventory.setNumber(currentQuantity - quantity);
                    } else {
                        throw new RuntimeException("Số lượng hiện tại không đủ để trừ");
                    }
                    break;
                case "set":
                    inventory.setNumber(quantity);
                    break;
                default:
                    throw new RuntimeException("Loại thao tác không hợp lệ");
            }

            inventoryRepository.save(inventory);
            return true;
        } else {
            // Nếu chưa có record trong Inventory, tạo mới
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setNumber(quantity);
            inventoryRepository.save(newInventory);
            return true;
        }
    }

    /**
     * Tạo hoặc cập nhật tồn kho cho sản phẩm
     */
    @Transactional
    public boolean createInventory(Integer productId, Integer quantity) {
        Optional<Inventory> existing = inventoryRepository.findById(productId);
        if (existing.isPresent()) {
            Inventory inv = existing.get();
            inv.setNumber(quantity);
            inventoryRepository.save(inv);
        } else {
            Inventory inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setNumber(quantity);
            inventoryRepository.save(inventory);
        }
        return true;
    }

    /**
     * Lấy danh sách tồn kho của tất cả sản phẩm
     */
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }
}