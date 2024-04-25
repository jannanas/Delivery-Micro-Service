package nl.tudelft.sem.template.example.integration;

import delivery_microservice.model.PrivateCourier;
import delivery_microservice.model.UpdateCourierRequest;
import nl.tudelft.sem.template.delivery.controllers.PrivateCourierController;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.CourierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PrivateCourierControllerTest {

    PrivateCourierController controller;

    CourierService serviceMock;

    AuthorisationService authMock;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        serviceMock = Mockito.mock(CourierService.class);
        authMock = Mockito.mock(AuthorisationService.class);
        controller = new PrivateCourierController(serviceMock, authMock);

        when(authMock.isValid(4L)).thenReturn(true);
        when(authMock.isValid(null)).thenReturn(false);
        when(authMock.isValid(1L)).thenReturn(true);
        when(authMock.isValid(-1015L)).thenReturn(false);
        when(authMock.isValid(1015L)).thenReturn(true);
        when(authMock.isValid(1016L)).thenReturn(true);
        when(authMock.isValid(1017L)).thenReturn(true);
        when(authMock.isValid(1018L)).thenReturn(true);
        when(authMock.isValid(1019L)).thenReturn(true);

        when(authMock.isUser(4L)).thenReturn(false);
        when(authMock.isUser(null)).thenReturn(false);
        when(authMock.isUser(1L)).thenReturn(true);
        when(authMock.isCourier(null)).thenReturn(false);
        when(authMock.isCourier(-1015L)).thenReturn(false);
        when(authMock.isCourier(1015L)).thenReturn(true);
        when(authMock.isCourier(1016L)).thenReturn(true);
        when(authMock.isCourier(1017L)).thenReturn(true);
        when(authMock.isCourier(1018L)).thenReturn(true);
        when(authMock.isCourier(1019L)).thenReturn(true);
    }

    @Test
    public void getCourierResponse403() {
        ResponseEntity<PrivateCourier> response = controller.getCourier(4L, 1015L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getCourierResponse400() {
        ResponseEntity<PrivateCourier> response = controller.getCourier(null, 1015L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        response = controller.getCourier(1L, null);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        response = controller.getCourier(1L, -1015L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getCourierResponse404() {
        when(serviceMock.getPrivateCourier(1015L)).thenReturn(Optional.of(new PrivateCourier()
                .courierId(1015L)
                .vendorId(158L)));
        when(serviceMock.getPrivateCourier(1016L)).thenReturn(Optional.empty());
        ResponseEntity<PrivateCourier> response = controller.getCourier(1L, 1016L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getCourierResponse200() {
        PrivateCourier courier = new PrivateCourier()
                .courierId(1015L)
                .vendorId(158L);
        when(serviceMock.getPrivateCourier(1015L)).thenReturn(Optional.of(courier));
        ResponseEntity<PrivateCourier> response = controller.getCourier(1L, 1015L);
        assertEquals(response, ResponseEntity.of(Optional.of(courier)));
    }

    @Test
    public void getCourierMultipleResponses() {
        PrivateCourier courier1 = new PrivateCourier()
                .courierId(1015L)
                .vendorId(158L);
        PrivateCourier courier2 = new PrivateCourier()
                .courierId(1016L)
                .vendorId(158L);
        PrivateCourier courier3 = new PrivateCourier()
                .courierId(1017L)
                .vendorId(82L);
        PrivateCourier courier4 = new PrivateCourier()
                .courierId(1018L)
                .vendorId(234L);
        when(serviceMock.getPrivateCourier(1015L)).thenReturn(Optional.of(courier1));
        when(serviceMock.getPrivateCourier(1016L)).thenReturn(Optional.of(courier2));
        when(serviceMock.getPrivateCourier(1017L)).thenReturn(Optional.of(courier3));
        when(serviceMock.getPrivateCourier(1018L)).thenReturn(Optional.of(courier4));
        when(serviceMock.getPrivateCourier(1019L)).thenReturn(Optional.empty());
        ResponseEntity<PrivateCourier> response = controller.getCourier(1L, 1015L);
        assertEquals(response, ResponseEntity.of(Optional.of(courier1)));
        response = controller.getCourier(1L, 1016L);
        assertEquals(response, ResponseEntity.of(Optional.of(courier2)));
        response = controller.getCourier(4L, 1019L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
        response = controller.getCourier(1L, 1017L);
        assertEquals(response, ResponseEntity.of(Optional.of(courier3)));
        response = controller.getCourier(1L, 1018L);
        assertEquals(response, ResponseEntity.of(Optional.of(courier4)));
        response = controller.getCourier(1L, 1019L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Test
    public void updateCourierResponse403() {
        UpdateCourierRequest update = Mockito.mock(UpdateCourierRequest.class);
        when(update.getVendorId()).thenReturn(160L);
        ResponseEntity<PrivateCourier> response = controller.updateCourier(4L, 1015L, update);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateCourierResponse400() {
        UpdateCourierRequest update = Mockito.mock(UpdateCourierRequest.class);
        when(update.getVendorId()).thenReturn(160L);
        ResponseEntity<PrivateCourier> response = controller.updateCourier(null, 1015L, update);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        response = controller.updateCourier(1L, null, update);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        response = controller.updateCourier(1L, -1015L, update);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void updateCourierResponse404() {
        UpdateCourierRequest update = Mockito.mock(UpdateCourierRequest.class);
        when(update.getVendorId()).thenReturn(160L);
        when(serviceMock.getPrivateCourier(1015L)).thenReturn(Optional.of(new PrivateCourier()
                .courierId(1015L)
                .vendorId(158L)));
        when(serviceMock.getPrivateCourier(1016L)).thenReturn(Optional.empty());
        ResponseEntity<PrivateCourier> response = controller.updateCourier(1L, 1016L, update);
        PrivateCourier pv = new PrivateCourier()
                .courierId(1016L)
                .vendorId(update.getVendorId());
        assertEquals(response, ResponseEntity.of(Optional.of(pv)));
    }

    @Test
    public void updateCourierResponse200() {
        UpdateCourierRequest update = Mockito.mock(UpdateCourierRequest.class);
        when(update.getVendorId()).thenReturn(160L);
        PrivateCourier courier = new PrivateCourier()
                .courierId(1015L)
                .vendorId(158L);
        when(serviceMock.getPrivateCourier(1015L)).thenReturn(Optional.of(courier));
        ResponseEntity<PrivateCourier> response = controller.getCourier(1L, 1015L);
        assertEquals(response, ResponseEntity.of(Optional.of(courier)));
        response = controller.updateCourier(1L, 1015L, update);
        PrivateCourier updatedCourier = new PrivateCourier()
                .courierId(1015L)
                .vendorId(160L);
        assertEquals(response, ResponseEntity.of(Optional.of(updatedCourier)));
    }

    @Test
    public void updateCourierMultipleResponses() {
        UpdateCourierRequest update1 = Mockito.mock(UpdateCourierRequest.class);
        when(update1.getVendorId()).thenReturn(160L);
        UpdateCourierRequest update2 = Mockito.mock(UpdateCourierRequest.class);
        when(update2.getVendorId()).thenReturn(95L);
        UpdateCourierRequest update3 = Mockito.mock(UpdateCourierRequest.class);
        when(update3.getVendorId()).thenReturn(215L);

        PrivateCourier courier1 = new PrivateCourier().courierId(1015L).vendorId(158L);
        PrivateCourier updatedcourier1 = new PrivateCourier().courierId(1015L).vendorId(95L);
        when(serviceMock.getPrivateCourier(1015L)).thenReturn(Optional.of(courier1));
        ResponseEntity<PrivateCourier> response = controller.updateCourier(1L, 1015L, update2);
        assertEquals(response, ResponseEntity.of(Optional.of(updatedcourier1)));

        PrivateCourier courier2 = new PrivateCourier().courierId(1016L).vendorId(158L);
        when(serviceMock.getPrivateCourier(1016L)).thenReturn(Optional.of(courier2));
        controller.updateCourier(1L, 1016L, update2);
        response = controller.updateCourier(1L, 1016L, update1);
        assertEquals(response, ResponseEntity.of(Optional.of(courier2)));

        PrivateCourier courier3 = new PrivateCourier().courierId(1017L).vendorId(82L);
        when(serviceMock.getPrivateCourier(1017L)).thenReturn(Optional.of(courier3));
        controller.updateCourier(1L, 1017L, update1);
        controller.updateCourier(1L, 1017L, update2);
        response = controller.updateCourier(1L, 1017L, update3);
        PrivateCourier updatedcourier3 = new PrivateCourier().courierId(1017L).vendorId(215L);
        assertEquals(response, ResponseEntity.of(Optional.of(updatedcourier3)));

        when(serviceMock.getPrivateCourier(1019L)).thenReturn(Optional.empty());
        response = controller.updateCourier(4L, 1019L, update1);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }
}
