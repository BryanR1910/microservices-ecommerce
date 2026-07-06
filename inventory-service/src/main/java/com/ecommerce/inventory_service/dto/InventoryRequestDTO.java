package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequestDTO(
        @NotBlank String sku,

        @NotNull @PositiveOrZero(message = "La cantidad no puede ser menor a cero")
        Integer quantity) {}
