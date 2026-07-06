package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public InventoryResponseDTO createInventory(InventoryRequestDTO request) {
        boolean exists = inventoryRepository.existsBySku(request.sku());
        if (exists) {
            throw new RuntimeException("El inventario para el SKU " + request.sku() + " ya existe");
        }

        Inventory inventory = inventoryMapper.toModel(request);
        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("Inventario creado para el SKU: {}", request.sku());

        return inventoryMapper.toResponse(savedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) throw new ResourceNotFoundException("Inventory", "id", id);
        inventoryRepository.deleteById(id);
    }

    @Override
    public List<InventoryResponseDTO> getAllInventory() {
        List<Inventory> inventory = inventoryRepository.findAll();
        return inventory.stream().map(inventoryMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String sku, Integer quantity) {
        return inventoryRepository
                .findBySku(sku)
                .map(inventory -> inventory.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    public InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO request) {
        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        inventory.setSku(request.sku());
        inventory.setQuantity(request.quantity());

        Inventory updateInventory = inventoryRepository.save(inventory);
        log.info("Inventario actualizado para el ID: {}", id);
        return inventoryMapper.toResponse(updateInventory);
    }
}
