package nl.tudelft.sem.template.example.integration;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.controllers.CourierController;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import nl.tudelft.sem.template.delivery.services.CourierLocationService;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.external.FakeCourierLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class CourierControllerTest {

    CourierController controller;

    CourierLocationService gpsService;

    AuthorisationService authMock;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        gpsService = Mockito.mock(FakeCourierLocationService.class);
        authMock = Mockito.mock(AuthorisationService.class);
        controller = new CourierController(gpsService, authMock);

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
        ResponseEntity<Location> response = controller.getCourierLocation(4L, 1015L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getCourierLocationResponse400() {
        ResponseEntity<Location> response = controller.getCourierLocation(null, 1015L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        response = controller.getCourierLocation(1L, null);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        response = controller.getCourierLocation(1L, -1015L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getCourierLocationResponse404() {
        try {
            doThrow(new EntityNotFoundException("courier", 1015L)).when(gpsService).getLocationOfCourier(1015L);
            ResponseEntity<Location> response = controller.getCourierLocation(1L, 1015L);
            assertEquals(response, new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (EntityNotFoundException e) {
            fail();
        }
    }

    @Test
    public void getCourierLocationResponse200() {
        try {
            Location location = new Location()
                    .address("Address 1")
                    .city("City 1")
                    .country("Country 1")
                    .postalCode("111AA");
            when(gpsService.getLocationOfCourier(1015L)).thenReturn(location);
            ResponseEntity<Location> response = controller.getCourierLocation(1L, 1015L);
            assertEquals(response, ResponseEntity.of(Optional.of(location)));
        } catch (EntityNotFoundException e) {
            fail();
        }
    }

    @Test
    public void getCourierLocationMultipleResponses() {
        Location location1 = new Location()
                .address("Address 1")
                .city("City 1")
                .country("Country 1")
                .postalCode("111AA");
        Location location2 = new Location()
                .address("Address 2")
                .city("City 2")
                .country("Country 2")
                .postalCode("222AA");
        Location location3 = new Location()
                .address("Address 3")
                .city("City 1")
                .country("Country 1")
                .postalCode("311AA");
        Location location4 = new Location()
                .address("Address 1")
                .city("City 1")
                .country("Country 1")
                .postalCode("111AD");
        try {
            when(gpsService.getLocationOfCourier(1015L)).thenReturn(location1);
            when(gpsService.getLocationOfCourier(1016L)).thenReturn(location2);
            when(gpsService.getLocationOfCourier(1017L)).thenReturn(location3);
            when(gpsService.getLocationOfCourier(1018L)).thenReturn(location4);
            doThrow(new EntityNotFoundException("courier", 1019L)).when(gpsService).getLocationOfCourier(1019L);
            ResponseEntity<Location> response = controller.getCourierLocation(1L, 1015L);
            assertEquals(response, ResponseEntity.of(Optional.of(location1)));
            response = controller.getCourierLocation(1L, 1016L);
            assertEquals(response, ResponseEntity.of(Optional.of(location2)));
            response = controller.getCourierLocation(4L, 1019L);
            assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
            response = controller.getCourierLocation(1L, 1017L);
            assertEquals(response, ResponseEntity.of(Optional.of(location3)));
            response = controller.getCourierLocation(1L, 1018L);
            assertEquals(response, ResponseEntity.of(Optional.of(location4)));
            response = controller.getCourierLocation(1L, 1019L);
            assertEquals(response, new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (EntityNotFoundException e) {
            fail();
        }
    }

}
