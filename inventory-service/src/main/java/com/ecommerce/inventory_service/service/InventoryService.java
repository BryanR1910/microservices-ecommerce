package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import java.util.List;

public interface InventoryService {
    boolean isInStock(String sku, Integer quantity);

    InventoryResponseDTO createInventory(InventoryRequestDTO request);

    List<InventoryResponseDTO> getAllInventory();

    InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO request);

    void deleteInventory(Long id);
}
