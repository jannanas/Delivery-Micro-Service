package nl.tudelft.sem.template.delivery.services;


import delivery_microservice.model.Delay;
import delivery_microservice.model.Delivery;
import delivery_microservice.model.Location;
import delivery_microservice.model.Locations;
import delivery_microservice.model.Times;
import delivery_microservice.model.UpdateDeliveryRequest;
import nl.tudelft.sem.template.delivery.database.DelayRepository;
import nl.tudelft.sem.template.delivery.database.DeliveryRepository;
import nl.tudelft.sem.template.delivery.dtos.UpdateDeliveryDto;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import orders_microservice.ApiException;
import orders_microservice.api.OrderApi;
import orders_microservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import org.mockito.ArgumentCaptor;
import users_microservice.api.VendorApi;
import users_microservice.model.Vendor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class DefaultDeliveryServiceTest {
    private final DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
    private final OrderApi orderApi = mock(OrderApi.class);
    private final VendorApi vendorApi = mock(VendorApi.class);
    private final DelayRepository delayRepository = mock(DelayRepository.class);
    private final DeliveryService deliveryService = new DefaultDeliveryService(deliveryRepository,
            delayRepository, orderApi, vendorApi);
    private Delivery delivery = new Delivery();
    private Delivery createdDelivery = new Delivery();
    private Delivery differentDelivery = new Delivery();
    private Vendor vendor = new Vendor();
    private Order order = new Order();
    private Order updatedOrder = new Order();

    @BeforeEach
    void setup() {
        delivery.setDeliveryId(8L);
        delivery.setOrderId(8L);
        delivery.setVendorId(3L);
        delivery.setCourierId(14L);
        delivery.setRating(4);

        Locations deliveryLocations = new Locations()
                .vendorCountry("Netherlands")
                .vendorCity("Delft")
                .vendorAddress("Mekelweg 3")
                .vendorPostalCode("2628 CD")
                .customerCountry("Netherlands")
                .customerCity("Delft")
                .customerAddress("Professor Schermerhornstraat 9")
                .customerPostalCode("2628 CN");

        delivery.setLocations(deliveryLocations);

        Times deliveryTimes = new Times();
        deliveryTimes.setEstimatedPickupTime(OffsetDateTime.of(2024, 1, 6, 11, 21, 18, 0, ZoneOffset.UTC));
        deliveryTimes.setEstimatedDeliveryTime(deliveryTimes.getEstimatedPickupTime().plusHours(1));
        deliveryTimes.setActualPickupTime(OffsetDateTime.of(2024, 1, 6, 11, 18, 18, 0, ZoneOffset.UTC));
        deliveryTimes.setActualDeliveryTime(deliveryTimes.getActualPickupTime().plusMinutes(41));
        deliveryTimes.setDelays(new ArrayList<>());
        delivery.setTimes(deliveryTimes);

        delivery.setDelivered(true);

        createdDelivery.setDeliveryId(8L);
        createdDelivery.setOrderId(8L);
        createdDelivery.setVendorId(3L);

        Locations createdDeliveryLocations = new Locations()
                .vendorCountry("Netherlands")
                .vendorCity("Delft")
                .vendorAddress("Mekelweg 3")
                .vendorPostalCode("2628 CD")
                .customerCountry("Netherlands")
                .customerCity("Delft")
                .customerAddress("Professor Schermerhornstraat 9")
                .customerPostalCode("2628 CN");

        createdDelivery.setLocations(createdDeliveryLocations);

        Times createdDeliveryTimes = new Times();
        createdDeliveryTimes.setEstimatedPickupTime(OffsetDateTime.of(2024, 1, 6, 11, 21, 18, 0, ZoneOffset.UTC));
        createdDeliveryTimes.setEstimatedDeliveryTime(createdDeliveryTimes.getEstimatedPickupTime().plusHours(1));
        createdDeliveryTimes.setDelays(new ArrayList<>());
        createdDelivery.setTimes(createdDeliveryTimes);

        createdDelivery.setDelivered(false);

        differentDelivery.setDeliveryId(9L);
        differentDelivery.setOrderId(9L);
        differentDelivery.setVendorId(3L);
        differentDelivery.setCourierId(17L);

        Locations differentDeliveryLocations = new Locations()
                .vendorCountry("Netherlands")
                .vendorCity("Delft")
                .vendorAddress("Mekelweg 3")
                .vendorPostalCode("2628 CD")
                .customerCountry("Netherlands")
                .customerCity("Delft")
                .customerAddress("Professor Schermerhornstraat 9")
                .customerPostalCode("2628 CN");
        differentDelivery.setLocations(differentDeliveryLocations);

        Times differentDeliveryTimes = new Times();
        differentDeliveryTimes.setEstimatedPickupTime(OffsetDateTime.of(2024, 1, 6, 11, 21, 18, 0, ZoneOffset.UTC));
        differentDeliveryTimes.setEstimatedDeliveryTime(differentDeliveryTimes.getEstimatedPickupTime().plusHours(1));
        differentDeliveryTimes.setDelays(new ArrayList<>());
        differentDelivery.setTimes(differentDeliveryTimes);

        differentDelivery.setDelivered(false);

        vendor.setId(3L);
        vendor.setName("John");
        vendor.setSurname("Doe");
        vendor.setEmail("johndoe@gmail.com");
        vendor.setOpeningHour(OffsetDateTime.of(2024, 1, 6, 11, 21, 18, 0, ZoneOffset.UTC));
        vendor.setClosingHour(vendor.getOpeningHour().plusHours(12));

        users_microservice.model.Location vendorLocation = new users_microservice.model.Location()
                .country("Netherlands")
                .city("Delft")
                .street("Mekelweg 3")
                .streetNumber("2628 CD");
        vendor.setLocation(vendorLocation);
        vendor.setPastOrders(new ArrayList<>());

        order.setOrderID(8L);
        order.setCustomerID(43L);
        order.vendorID(3L);
        order.setDishes(new ArrayList<>());
        order.setPrice(42.00f);

        orders_microservice.model.Location orderLocation = new orders_microservice.model.Location()
                .country("Netherlands")
                .city("Delft")
                .address("Professor Schermerhornstraat 9")
                .postalCode("2628 CN");

        order.setLocation(orderLocation);

        order.setStatus(Order.StatusEnum.PENDING);
        order.setRating(4);
        order.setCourierRating(5);

        updatedOrder.setOrderID(order.getOrderID());
        updatedOrder.setCustomerID(order.getCustomerID());
        updatedOrder.setVendorID(order.getVendorID());
        updatedOrder.setDishes(order.getDishes());
        updatedOrder.setPrice(order.getPrice());
        updatedOrder.setLocation(order.getLocation());
        updatedOrder.setStatus(Order.StatusEnum.GIVEN_TO_COURIER);
        updatedOrder.setRating(order.getRating());
        updatedOrder.setCourierRating(order.getCourierRating());
    }

    @Test
    void exists() {
        when(deliveryRepository.existsById(1L)).thenReturn(true);
        assertTrue(deliveryService.exists(1L));
    }

    @Test
    void doesNotExist() {
        when(deliveryRepository.existsById(1L)).thenReturn(false);
        assertFalse(deliveryService.exists(1L));
    }

    @Test
    void getDelivery() {
        when(deliveryRepository.findById(8L)).thenReturn(Optional.ofNullable(this.delivery));
        assertEquals(deliveryService.getDelivery(8L).get(), delivery);
    }

    @Test
    void getDeliveryNonExisting() {
        when(deliveryRepository.findById(8L)).thenReturn(Optional.empty());
        assertTrue(deliveryService.getDelivery(8L).isEmpty());
    }

    @Test
    void getDeliveryByOrderId() {
        when(deliveryRepository.findDeliveryByOrderId(8L)).thenReturn(Optional.ofNullable(this.delivery));
        assertEquals(deliveryService.getDeliveryByOrderId(8L).get(), delivery);
    }

    @Test
    void getDeliveryByOrderIdNonExisting() {
        when(deliveryRepository.findDeliveryByOrderId(8L)).thenReturn(Optional.empty());
        assertTrue(deliveryService.getDeliveryByOrderId(8L).isEmpty());
    }

    @Test
    void getDeliveryByVendorId() {
        differentDelivery.setVendorId(8L);
        when(deliveryRepository.findAll()).thenReturn(List.of(delivery, differentDelivery));
        assertEquals(deliveryService.getDeliveryByVendorId(3L), List.of(delivery));
    }

    @Test
    void getDeliveryByVendorIdMultiple() {
        when(deliveryRepository.findAll()).thenReturn(List.of(delivery, differentDelivery));
        assertEquals(deliveryService.getDeliveryByVendorId(3L), List.of(delivery, differentDelivery));
    }

    @Test
    void getDeliveryByCourierId() {
        when(deliveryRepository.findAll()).thenReturn(List.of(delivery, differentDelivery));
        assertEquals(deliveryService.getDeliveryByCourierId(14L), List.of(delivery));
    }

    @Test
    void createDelivery() throws users_microservice.ApiException, EntityNotFoundException {
        when(vendorApi.vendorsVerifiedGet()).thenReturn(List.of(vendor));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(createdDelivery);

        Location customerLocation = new Location();
        customerLocation.setCountry("Netherlands");
        customerLocation.setCity("Delft");
        customerLocation.setAddress("Professor Schermerhornstraat 9");
        customerLocation.setPostalCode("2628 CN");

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);

        Delivery returnedDelivery = deliveryService.createDelivery(
                8, 3, customerLocation, createdDelivery.getTimes().getEstimatedPickupTime());

        verify(deliveryRepository, times(1)).save(captor.capture());

        Delivery captured = captor.getValue();
        captured.setDeliveryId(createdDelivery.getDeliveryId());

        assertEquals(returnedDelivery, createdDelivery);
        assertEquals(captured, createdDelivery);
    }

    @Test
    void createDeliveryNoEta() throws users_microservice.ApiException, EntityNotFoundException {
        when(vendorApi.vendorsVerifiedGet()).thenReturn(List.of(vendor));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(createdDelivery);

        Location customerLocation = new Location();
        customerLocation.setCountry("Netherlands");
        customerLocation.setCity("Delft");
        customerLocation.setAddress("Professor Schermerhornstraat 9");
        customerLocation.setPostalCode("2628 CN");

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);

        Delivery returnedDelivery = deliveryService.createDelivery(
            8, 3, customerLocation, null);

        verify(deliveryRepository, times(1)).save(captor.capture());

        Delivery captured = captor.getValue();
        captured.setDeliveryId(createdDelivery.getDeliveryId());

        assertNotEquals(captured.getTimes(), createdDelivery.getTimes());
        assertEquals(captured.getDeliveryId(), createdDelivery.getDeliveryId());
        assertEquals(captured.getRating(), createdDelivery.getRating());
        assertEquals(captured.getCourierId(), createdDelivery.getCourierId());
        assertEquals(captured.getVendorId(), createdDelivery.getVendorId());
        assertEquals(captured.getDelivered(), createdDelivery.getDelivered());
        assertEquals(captured.getLocations(), createdDelivery.getLocations());
    }

    @Test
    void createDeliveryVendorNotFound() throws users_microservice.ApiException {
        when(vendorApi.vendorsVerifiedGet()).thenReturn(List.of(vendor));

        Location customerLocation = new Location();
        customerLocation.setCountry("Netherlands");
        customerLocation.setCity("Delft");
        customerLocation.setAddress("Professor Schermerhornstraat 9");
        customerLocation.setPostalCode("2628 CN");

        assertThrows(EntityNotFoundException.class,
                () -> deliveryService.createDelivery(
                        8, 77, customerLocation, createdDelivery.getTimes().getEstimatedPickupTime()));
    }

    @Test
    void updateDelivery() throws ApiException, EntityNotFoundException {

        when(deliveryRepository.findById(8L)).thenReturn(Optional.ofNullable(createdDelivery));
        when(orderApi.orderOrderIDGet(delivery.getDeliveryId())).thenReturn(List.of(order));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(createdDelivery);

        var delay = new Delay();
        var delayList = List.of(delay);
        when(delayRepository.saveAll(any())).thenReturn(delayList);

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);

        UpdateDeliveryDto dto = UpdateDeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .courierId(delivery.getCourierId())
                .rating(delivery.getRating())
                .times(delivery.getTimes())
                .status(UpdateDeliveryRequest.StatusEnum.GIVEN_TO_COURIER)
                .delivered(delivery.getDelivered())
                .build();
        dto.getTimes().setDelays(List.of(delay));

        Delivery returned = deliveryService.updateDelivery(42L, dto);

        assertEquals(returned, delivery);

        verify(deliveryRepository, times(1)).save(captor.capture());
        verify(orderApi, times(1)).orderPut(42L, updatedOrder);
        verify(delayRepository, times(1)).saveAll(any());

        Delivery captured = captor.getValue();

        assertEquals(captured, delivery);
        assertSame(captured.getTimes().getDelays(), delayList);
    }

    @Test
    void updateNonExistingDelivery() throws ApiException, EntityNotFoundException {
        when(deliveryRepository.findById(8L)).thenReturn(Optional.empty());

        UpdateDeliveryDto dto = UpdateDeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .courierId(delivery.getCourierId())
                .rating(delivery.getRating())
                .times(delivery.getTimes())
                .status(UpdateDeliveryRequest.StatusEnum.GIVEN_TO_COURIER)
                .delivered(delivery.getDelivered())
                .build();

        assertThrows(EntityNotFoundException.class, () -> deliveryService.updateDelivery(42L, dto));
    }

    @Test
    void getUnassigned() {
        when(deliveryRepository.findDeliveriesByCourierIdNull()).thenReturn(List.of(createdDelivery));
        assertEquals(deliveryService.getUnassigned(), List.of(createdDelivery.getDeliveryId()));
    }

    @Test
    void getAllDeliveredDeliveries() {
        List<Delivery> deliveryList = new ArrayList<>();
        deliveryList.add(delivery);
        deliveryList.add(differentDelivery);

        when(deliveryRepository.findDeliveredDeliveries()).thenReturn(deliveryList);
        assertEquals(deliveryList, deliveryService.getAllDeliveredDeliveries());
    }
}