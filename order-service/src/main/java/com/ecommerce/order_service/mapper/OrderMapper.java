package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.dto.OrderLineItemsRequestDTO;
import com.ecommerce.order_service.dto.OrderLineItemsResponseDTO;
import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderLineItems;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderRequestDTO orderRequest);

    OrderLineItems toOrderLineItems(OrderLineItemsRequestDTO orderLineItemsRequest);

    OrderResponseDTO toOrderResponse(Order order);

    OrderLineItemsResponseDTO toOrderLineItemsResponse(OrderLineItems orderLineItems);
}
