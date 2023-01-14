package ke.or.expd.orderservice.service;

import ke.or.expd.orderservice.model.dto.OrderRequest;

public interface OrderService {
    String placeOrder(OrderRequest orderRequest);
}
