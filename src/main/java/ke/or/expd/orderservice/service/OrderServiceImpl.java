package ke.or.expd.orderservice.service;

import ke.or.expd.orderservice.exception.OrderPlacementException;
import ke.or.expd.orderservice.model.dto.OrderLineItemDto;
import ke.or.expd.orderservice.model.dto.OrderRequest;
import ke.or.expd.orderservice.model.entity.Order;
import ke.or.expd.orderservice.model.entity.OrderLineItem;
import ke.or.expd.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public String placeOrder(OrderRequest orderRequest) {

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                .map(this::mapToOrderEntity)
                .toList();

        Order newOrder = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        Order savedOrder = orderRepository.save(newOrder);
        if (savedOrder.getId() != null)
            return "Order Placed Successfully";
        else
            throw new OrderPlacementException("Error Placing Order ");
    }

    private OrderLineItem mapToOrderEntity(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .id(null)
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .skuCode(orderLineItemDto.getSkuCode())
                .build();
    }
}
