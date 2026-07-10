package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderLineItems;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.client.InventoryClient;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@RefreshScope
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    // private final WebClient.Builder webClientBuilder;
    private final InventoryClient inventoryClient;

    @Value("${order.enabled:true}")
    private boolean ordersEnabled;

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) {
        if (!ordersEnabled) {
            log.warn("Pedido rechazado: Servicio deshabilitado por configuracion.");
            throw new RuntimeException("El servicio de pedidos esta actualmente en mantenimiento. Intente mas tarde.");
        }
        log.info("Colocando nueva orden...");

        // Mapeo manual de items para asegurar la lista
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList().stream()
                .map(orderMapper::toOrderLineItems)
                .toList();

        for (var item : orderLineItems) {
            String sku = item.getSku();
            Integer quantity = item.getQuantity();

            try {
                // webClientBuilder
                // .build()
                // .put()
                // .uri(
                //         "http://localhost:8082/api/v1/inventory/reduce/" + sku,
                //         uriBuilder -> uriBuilder
                //                 .queryParam("quantity", quantity)
                //                 .build())
                // .retrieve()
                // .bodyToMono(String.class)
                // .block();
                inventoryClient.reduceStock(sku, quantity);
            } catch (Exception e) {
                log.error("Error al reducir el stock para el producto {} : {}", sku, e.getMessage());
                throw new IllegalArgumentException(
                        "No se pudo procesar la order: Stock insuficiente o error de inventario");
            }
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderLineItems);

        // Guardamos y capturamos la entidad persistida
        Order savedOrder = orderRepository.save(order);
        log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Orden", "id", id));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orden", "id", id);
        }
        orderRepository.deleteById(id);
        log.info("Orden eliminada. ID: {}", id);
    }
}
