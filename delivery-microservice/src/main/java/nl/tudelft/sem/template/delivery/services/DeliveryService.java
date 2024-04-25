package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Delivery;
import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.dtos.UpdateDeliveryDto;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import orders_microservice.ApiException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface DeliveryService {
    boolean exists(long id);

    Optional<Delivery> getDelivery(long id);

    Optional<Delivery> getDeliveryByOrderId(long id);

    List<Delivery> getDeliveryByVendorId(long id);

    List<Delivery> getDeliveryByCourierId(long id);

    Delivery createDelivery(long orderId, long vendorId, Location customerLocation, OffsetDateTime estimatedPickUpTime)
            throws users_microservice.ApiException, EntityNotFoundException;

    Delivery updateDelivery(Long userId, UpdateDeliveryDto dto) throws EntityNotFoundException, ApiException;

    List<Long> getUnassigned();

    List<Delivery> getAllDeliveredDeliveries();
}