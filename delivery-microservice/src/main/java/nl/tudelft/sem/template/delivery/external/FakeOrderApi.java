package nl.tudelft.sem.template.delivery.external;

import orders_microservice.api.OrderApi;
import orders_microservice.model.Order;

import java.util.ArrayList;
import java.util.List;

public class FakeOrderApi extends OrderApi {
    @Override
    public List<Order> orderOrderIDGet(Long orderId) {
        return List.of(new Order()
                .orderID(orderId)
                .customerID(43L)
                .vendorID(3L)
                .dishes(new ArrayList<>())
                .price(42.00f)
                .location(new orders_microservice.model.Location()
                        .country("Netherlands")
                        .city("Delft")
                        .address("Professor Schermerhornstraat 9")
                        .postalCode("2628 CN"))
                .status(Order.StatusEnum.PENDING)
                .rating(4)
                .courierRating(5)
        );
    }

    public Order orderPut(Long userId, Order order) {
        return order;
    }
}
