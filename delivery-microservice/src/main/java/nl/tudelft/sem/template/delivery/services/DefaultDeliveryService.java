package nl.tudelft.sem.template.delivery.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import delivery_microservice.model.Delivery;
import delivery_microservice.model.Delay;
import delivery_microservice.model.Locations;
import delivery_microservice.model.Times;
import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.database.DelayRepository;
import nl.tudelft.sem.template.delivery.database.DeliveryRepository;
import nl.tudelft.sem.template.delivery.dtos.UpdateDeliveryDto;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import orders_microservice.ApiException;
import orders_microservice.api.OrderApi;
import orders_microservice.model.Order;
import org.springframework.stereotype.Service;
import users_microservice.api.VendorApi;
import users_microservice.model.Vendor;

@Service
public class DefaultDeliveryService implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DelayRepository delayRepository;
    private final OrderApi orderApi;
    private final VendorApi vendorApi;

    /**
     * Testing constructor to inject mocks.
     *
     * @param deliveryRepository passes repo mock
     * @param orderApi passes orderapi mock
     * @param vendorApi passes vendorapi mock
     */
    public DefaultDeliveryService(DeliveryRepository deliveryRepository,
            DelayRepository delayRepository, OrderApi orderApi, VendorApi vendorApi) {
        this.deliveryRepository = deliveryRepository;
        this.delayRepository = delayRepository;
        this.orderApi = orderApi;
        this.vendorApi = vendorApi;
    }

    public boolean exists(long id) {
        return deliveryRepository.existsById(id);
    }

    public Optional<Delivery> getDelivery(long id) {
        return deliveryRepository.findById(id);
    }


    /**
     * Gets a delivery by order id.
     *
     * @param id long, id of order
     * @return Delivery with order id equal to provided id
     */
    public Optional<Delivery> getDeliveryByOrderId(long id) {
        return deliveryRepository.findDeliveryByOrderId(id);
    }


    /**
     * Gets a delivery by vendor id.
     *
     * @param id long, id of vendor
     * @return Delivery with order id equal to provided id
     */
    public List<Delivery> getDeliveryByVendorId(long id) {
        return deliveryRepository.findAll().stream()
                .filter(x -> x.getVendorId() == id)
                .collect(Collectors.toList());
    }


    /**
     * Gets a delivery by courier id.
     *
     * @param id long, id of vendor
     * @return Delivery with order id equal to provided id
     */
    public List<Delivery> getDeliveryByCourierId(long id) {
        return deliveryRepository.findAll().stream()
                .filter(x -> x.getCourierId() == id)
                .collect(Collectors.toList());
    }

    /**
     * Creates a delivery.
     *
     * @param orderId the id of the order
     * @param vendorId the id of the vendor
     * @param customerLocation the location to deliver to
     * @param estimatedPickUpTime the pickup time
     * @return the new delivery object
     */
    public Delivery createDelivery(long orderId, long vendorId, Location customerLocation,
                                   OffsetDateTime estimatedPickUpTime) throws users_microservice.ApiException,
            EntityNotFoundException {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setVendorId(vendorId);

        delivery.setDelivered(false);

        Times times = new Times();
        if (estimatedPickUpTime == null) {
            estimatedPickUpTime = OffsetDateTime.now();
        }

        times.setEstimatedPickupTime(estimatedPickUpTime);
        times.setEstimatedDeliveryTime(estimatedPickUpTime.plusHours(1));
        times.setDelays(new ArrayList<>());

        delivery.setTimes(times);

        List<Vendor> vendors = vendorApi.vendorsVerifiedGet().stream()
                .filter(x -> x.getId() == vendorId)
                .collect(Collectors.toList());

        if (vendors.size() == 0) {
            throw new EntityNotFoundException(Vendor.class, vendorId);
        }

        Vendor vendor = vendors.get(0);

        Locations locations = new Locations()
                .vendorCountry(vendor.getLocation().getCountry())
                .vendorCity(vendor.getLocation().getCity())
                .vendorAddress(vendor.getLocation().getStreet())
                .vendorPostalCode(vendor.getLocation().getStreetNumber())
                .customerCountry(customerLocation.getCountry())
                .customerCity(customerLocation.getCity())
                .customerAddress(customerLocation.getAddress())
                .customerPostalCode(customerLocation.getPostalCode());

        delivery.setLocations(locations);

        return deliveryRepository.save(delivery);
    }

    /**
     * Updates a delivery.
     *
     * @param dto transfers data to the controller
     * @return the updated delivery
     * @throws EntityNotFoundException if something is wrong
     */
    public Delivery updateDelivery(Long userId, UpdateDeliveryDto dto) throws EntityNotFoundException, ApiException {
        Delivery delivery = deliveryRepository.findById(dto.getDeliveryId())
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, dto.getDeliveryId()));

        if (dto.getCourierId() != null) {
            delivery.setCourierId(dto.getCourierId());
        }

        if (dto.getRating() != null) {
            delivery.setRating(dto.getRating());
        }

        if (dto.getTimes() != null) {
            dto.getTimes().setDelays(delayRepository.saveAll(dto.getTimes().getDelays()));
            delivery.setTimes(dto.getTimes());
        }

        if (dto.getStatus() != null) {
            Order order = orderApi.orderOrderIDGet(delivery.getOrderId()).get(0);

            order.setStatus(Enum.valueOf(Order.StatusEnum.class, dto.getStatus().name()));

            orderApi.orderPut(userId, order);
        }

        if (dto.getDelivered() != null) {
            delivery.setDelivered(dto.getDelivered());
        }

        return deliveryRepository.save(delivery);
    }

    /**
     * Gets deliveries without a vendor.
     *
     * @return List of DeliveryIds with unassigned vendors
     */
    public List<Long> getUnassigned() {
        var unassignedDeliveries = deliveryRepository.findDeliveriesByCourierIdNull();
        return unassignedDeliveries.stream()
            .map(Delivery::getDeliveryId)
            .collect(Collectors.toList());
    }

    public List<Delivery> getAllDeliveredDeliveries() {
        List<Delivery> deliveredList = deliveryRepository.findDeliveredDeliveries();
        return deliveredList;
    }
}
