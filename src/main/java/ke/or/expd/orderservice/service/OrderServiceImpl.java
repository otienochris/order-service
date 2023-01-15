package ke.or.expd.orderservice.service;

import ke.or.expd.orderservice.exception.OrderPlacementException;
import ke.or.expd.orderservice.model.dto.InventoryInquiryResponse;
import ke.or.expd.orderservice.model.dto.OrderLineItemDto;
import ke.or.expd.orderservice.model.dto.OrderRequest;
import ke.or.expd.orderservice.model.entity.Order;
import ke.or.expd.orderservice.model.entity.OrderLineItem;
import ke.or.expd.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    public static final String INVENTORY_SERVICE_BASE_URL = "http://localhost:8082/inventory-service/api/v1/inventories";
    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Override
    public String placeOrder(OrderRequest orderRequest) {

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                .map(this::mapToOrderEntity)
                .toList();

        Order newOrder = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        // call inventory service and place order when product is in store.
        List<String> skuCodes = newOrder.getOrderLineItems().stream().map(OrderLineItem::getSkuCode).toList();

        InventoryInquiryResponse[] inventoryInquiryResponses = webClient.get()
                .uri(INVENTORY_SERVICE_BASE_URL, uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryInquiryResponse[].class)
                .block();

        boolean allProductsAreInStock = Arrays.stream(inventoryInquiryResponses).allMatch(InventoryInquiryResponse::getIsInStock);

        if (allProductsAreInStock) {
            orderRepository.save(newOrder);
            return "Order Placed Successfully";
        }else
            throw new OrderPlacementException("Products are not in stock, please try again");
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
