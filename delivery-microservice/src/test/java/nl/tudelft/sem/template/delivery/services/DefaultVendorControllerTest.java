package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import delivery_microservice.model.PrivateCourier;
import delivery_microservice.model.RadiusVendorPair;
import delivery_microservice.model.UpdateVendorDeliveryRadiusRequest;
import nl.tudelft.sem.template.delivery.controllers.VendorController;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class DefaultVendorControllerTest {

    RadiusVendorPairService radiusVendorPairService;
    CourierService courierService;
    AuthorisationService authorisationService;
    VendorController vendorController;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setup() {
        radiusVendorPairService = Mockito.mock(RadiusVendorPairService.class);
        courierService = Mockito.mock(CourierService.class);
        authorisationService = Mockito.mock(AuthorisationService.class);
        vendorController = new VendorController(radiusVendorPairService, courierService, authorisationService);
    }

    /**
     * Test get couriers by vendor.
     */
    @Test
    public void testGetCouriersByVendor() {
        var privateCouriers = Collections.singletonList(new PrivateCourier().vendorId(3L).courierId(3L));

        when(courierService.getAllPrivateCouriersByVendor(anyLong())).thenReturn(privateCouriers);
        when(authorisationService.isUser(9L)).thenReturn(true);
        when(authorisationService.isVendor(1L)).thenReturn(true);


        ResponseEntity<List<PrivateCourier>> response = vendorController.getCouriersByVendor(9L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(privateCouriers, response.getBody());
    }

    /**
     * Test get couriers by vendor 400.
     */
    @Test
    public void testGetCouriersByVendor400() {
        ResponseEntity<List<PrivateCourier>> response = vendorController.getCouriersByVendor(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test get couriers by vendor 403.
     */
    @Test
    public void testGetCouriersByVendor403() {
        when(authorisationService.isUser(anyLong())).thenReturn(false);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);

        ResponseEntity<List<PrivateCourier>> response = vendorController.getCouriersByVendor(1L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Test get couriers by vendor 404.
     */
    @Test
    public void testGetCouriersByVendor404() {
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(false);

        ResponseEntity<List<PrivateCourier>> response = vendorController.getCouriersByVendor(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    /**
     * Test get delivery radii.
     */
    @Test
    public void testGetDeliveryRadii() {

        var pairs = List.of(new RadiusVendorPair().vendorId(1L).radius(2), new RadiusVendorPair().vendorId(2L).radius(3));

        when(radiusVendorPairService.getAllRadiusVendorPairs()).thenReturn(pairs);
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);


        var response = vendorController.getDeliveryRadii(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals(pairs.get(0).getVendorId(), response.getBody().get(0).getVendorID());
        assertEquals(pairs.get(0).getRadius(), response.getBody().get(0).getRadius());
        assertEquals(pairs.get(1).getVendorId(), response.getBody().get(1).getVendorID());
        assertEquals(pairs.get(1).getRadius(), response.getBody().get(1).getRadius());

    }

    /**
     * Test get delivery radii 403.
     */
    @Test
    public void testGetDeliveryRadii403() {
        when(authorisationService.isUser(anyLong())).thenReturn(false);

        var response = vendorController.getDeliveryRadii(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Test get vendor delivery radius.
     */
    @Test
    public void testGetVendorDeliveryRadius() {
        RadiusVendorPair radiusVendorPair = new RadiusVendorPair();
        radiusVendorPair.setVendorId(1L);
        radiusVendorPair.setRadius(2);

        when(radiusVendorPairService.getRadiusVendorPair(anyLong())).thenReturn(java.util.Optional.of(radiusVendorPair));
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);

        var response = vendorController.getVendorDeliveryRadius(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(radiusVendorPair.getRadius(), Objects.requireNonNull(response.getBody()).getRadius());
    }


    /**
     * Test get vendor delivery radius 403.
     */
    @Test
    public void testGetVendorDeliveryRadius403() {
        when(authorisationService.isUser(anyLong())).thenReturn(false);

        var response = vendorController.getVendorDeliveryRadius(1L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Test get vendor delivery radius 404.
     */
    @Test
    public void testGetVendorDeliveryRadius404() {
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);
        when(radiusVendorPairService.getRadiusVendorPair(anyLong())).thenReturn(java.util.Optional.empty());

        var response = vendorController.getVendorDeliveryRadius(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test is customer in range from vendor.
     */
    @Test
    public void testIsCustomerInRangeFromVendor() throws LocationNotFoundException {
        when(radiusVendorPairService.vendorCustomerLocationIsValid(anyLong(), Mockito.any(), Mockito.any())).thenReturn(
            true);
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);

        var customerLocation = new Location();
        var vendorLocation = new Location();

        var response = vendorController.isCustomerInRangeFromVendor(1L, 1L, customerLocation, vendorLocation);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, Objects.requireNonNull(response.getBody()).getIsInRange());
    }

    /**
     * Test is customer in range from vendor.
     */
    @Test
    public void testIsCustomerInRangeFromVendorException() throws LocationNotFoundException {
        when(radiusVendorPairService.vendorCustomerLocationIsValid(anyLong(), Mockito.any(), Mockito.any()))
                .thenThrow(new LocationNotFoundException(new Location()));
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);

        var customerLocation = new Location();
        var vendorLocation = new Location();

        var response = vendorController.isCustomerInRangeFromVendor(1L, 1L, customerLocation, vendorLocation);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test is customer in range from vendor 400.
     */
    @Test
    public void testIsCustomerInRangeFromVendor400() {
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);

        var response = vendorController.isCustomerInRangeFromVendor(1L, 1L, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test is customer in range from vendor 403.
     */
    @Test
    public void testIsCustomerInRangeFromVendor403() {
        when(authorisationService.isUser(anyLong())).thenReturn(false);

        var response = vendorController.isCustomerInRangeFromVendor(1L, 1L, null, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Test is customer in range from vendor 404.
     */
    @Test
    public void testIsCustomerInRangeFromVendor404() throws LocationNotFoundException {
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(radiusVendorPairService.vendorCustomerLocationIsValid(anyLong(), Mockito.any(), Mockito.any())).thenReturn(
            false);

        var response = vendorController.isCustomerInRangeFromVendor(1L, 1L, null, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test is customer in range from vendor 404.
     */
    @Test
    public void testIsCustomerInRangeFromVendor404_LocationNotFound() throws LocationNotFoundException {
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(radiusVendorPairService.vendorCustomerLocationIsValid(anyLong(), Mockito.any(), Mockito.any())).thenThrow(
            new LocationNotFoundException(new Location()));

        var response = vendorController.isCustomerInRangeFromVendor(1L, 1L, null, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test update vendor delivery radius.
     */
    @Test
    public void testUpdateVendorDeliveryRadius() {
        RadiusVendorPair radiusVendorPair = new RadiusVendorPair();
        radiusVendorPair.setVendorId(1L);
        radiusVendorPair.setRadius(2);

        when(radiusVendorPairService.saveRadiusVendorPair(Mockito.any())).thenReturn(radiusVendorPair);
        when(authorisationService.isUser(anyLong())).thenReturn(true);
        when(authorisationService.isVendor(anyLong())).thenReturn(true);

        var response = vendorController.updateVendorDeliveryRadius(1L, 1L, new UpdateVendorDeliveryRadiusRequest(2));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(radiusVendorPair.getRadius(), Objects.requireNonNull(response.getBody()).getRadius());
    }

    /**
     * Test update vendor delivery radius 400.
     */
    @Test
    public void testUpdateVendorDeliveryRadius400() {
        var response = vendorController.updateVendorDeliveryRadius(null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test update vendor delivery radius 403.
     */
    @Test
    public void testUpdateVendorDeliveryRadius403() {
        when(authorisationService.isVendor(1L)).thenReturn(false);
        when(authorisationService.isVendor(2L)).thenReturn(true);


        var response = vendorController.updateVendorDeliveryRadius(1L, 2L, new UpdateVendorDeliveryRadiusRequest(2));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Test update vendor delivery radius 403 different vendor.
     */
    @Test
    public void testUpdateVendorDeliveryRadius403WrongVendor() {
        when(authorisationService.isVendor(1L)).thenReturn(true);
        when(authorisationService.isVendor(2L)).thenReturn(true);

        var response = vendorController.updateVendorDeliveryRadius(1L, 2L, new UpdateVendorDeliveryRadiusRequest(2));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Test update vendor delivery radius 404.
     */
    @Test
    public void testUpdateVendorDeliveryRadius404() {
        when(authorisationService.isVendor(1L)).thenReturn(true);
        when(authorisationService.isVendor(2L)).thenReturn(false);
        when(radiusVendorPairService.saveRadiusVendorPair(Mockito.any())).thenReturn(null);

        var response = vendorController.updateVendorDeliveryRadius(1L, 2L, new UpdateVendorDeliveryRadiusRequest(2));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
