package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.PrivateCourier;
import nl.tudelft.sem.template.delivery.database.PrivateCourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import users_microservice.ApiException;
import users_microservice.api.CourierApi;
import users_microservice.model.Courier;
import users_microservice.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class DefaultCourierServiceTest {
    private final PrivateCourierRepository privateCourierRepository = mock(PrivateCourierRepository.class);
    private final CourierApi courierApi = mock(CourierApi.class);
    private final CourierService courierService = new DefaultCourierService(privateCourierRepository, courierApi);

    private final PrivateCourier privateCourier = new PrivateCourier();
    private final PrivateCourier anotherPrivateCourier = new PrivateCourier();
    private final PrivateCourier lastPrivateCourier = new PrivateCourier();
    private final Courier publicCourier = new Courier();

    @BeforeEach
    void setup() {
        privateCourier.setCourierId(1L);
        privateCourier.setVendorId(5L);

        anotherPrivateCourier.setCourierId(10L);
        anotherPrivateCourier.setVendorId(5L);

        lastPrivateCourier.setCourierId(11L);
        lastPrivateCourier.setVendorId(6L);

        publicCourier.setId(2L);
        publicCourier.setName("Jannes");
        publicCourier.setSurname("Kelso");
        publicCourier.setEmail("j@k.g");
        publicCourier.setRating(10.0);
        publicCourier.setPastOrders(new ArrayList<Order>());
        publicCourier.setVerified(true);
        publicCourier.setPaymentMethods(new ArrayList<Object>());
    }

    @Test
    void privateCourierExistsTrueTest() {
        when(privateCourierRepository.existsById(1L)).thenReturn(true);
        assertTrue(courierService.privateCourierExists(1L));
    }

    @Test
    void privateCourierExistsFalseTest() {
        when(privateCourierRepository.existsById(2L)).thenReturn(false);
        assertFalse(courierService.privateCourierExists(2L));
    }

    @Test
    void getPrivateCourierSuccessTest() {
        when(privateCourierRepository.findById(1L)).thenReturn(Optional.of(privateCourier));
        assertEquals(privateCourier, courierService.getPrivateCourier(1L).get());
    }

    @Test
    void getPrivateCourierFailureTest() {
        when(privateCourierRepository.findById(2L)).thenReturn(Optional.empty());
        assertTrue(courierService.getPrivateCourier(2L).isEmpty());
    }

    @Test
    void getSomePrivateCourierByVendorSuccessTest() {
        when(privateCourierRepository.findOneByVendorId(5L)).thenReturn(privateCourier);
        assertEquals(privateCourier, courierService.getSomePrivateCourierByVendor(5L).get());
    }

    @Test
    void getSomePrivateCourierByVendorFailureTest() {
        when(privateCourierRepository.findOneByVendorId(6L)).thenReturn(null);
        assertTrue(courierService.getSomePrivateCourierByVendor(6L).isEmpty());
    }

    @Test
    void getAllPrivateCouriersByVendorTest() {
        List<PrivateCourier> couriers = new ArrayList<>();
        couriers.add(privateCourier);
        couriers.add(anotherPrivateCourier);

        when(privateCourierRepository.findAllByVendorId(5L)).thenReturn(couriers);
        assertEquals(couriers, courierService.getAllPrivateCouriersByVendor(5L));
    }

    @Test
    void savePrivateCourierTest() {
        when(privateCourierRepository.save(privateCourier)).thenReturn(privateCourier);
        assertEquals(privateCourier, courierService.savePrivateCourier(privateCourier));
        verify(privateCourierRepository, times(1)).save(privateCourier);
    }

    @Test
    void getSomePublicCourierSuccessTest() throws ApiException {
        List<Courier> couriers = new ArrayList<>();
        couriers.add(publicCourier);
        when(courierApi.couriersGet()).thenReturn(couriers);
        assertEquals(publicCourier, courierService.getSomePublicCourier().get());
    }

    @Test
    void getSomePublicCourierFailureTest() throws ApiException {
        when(courierApi.couriersGet()).thenThrow(new ApiException());
        assertTrue(courierService.getSomePublicCourier().isEmpty());
    }

    @Test
    void updatePublicCourierTest() throws ApiException {
        when(courierApi.couriersIdPut(2L, publicCourier)).thenReturn(publicCourier);
        assertEquals(publicCourier, courierService.updatePublicCourier(publicCourier));
    }
}
