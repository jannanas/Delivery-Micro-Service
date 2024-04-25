package nl.tudelft.sem.template.example.integration;

import delivery_microservice.model.Delivery;
import delivery_microservice.model.Times;
import delivery_microservice.model.Delay;
import delivery_microservice.model.GetCurrentDefaultRadius200Response;
import delivery_microservice.model.SetDefaultRadiusRadiusParameter;
import delivery_microservice.model.RadiusVendorPair;
import delivery_microservice.model.Analytics;
import nl.tudelft.sem.template.delivery.controllers.AdminController;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.DeliveryService;
import nl.tudelft.sem.template.delivery.services.RadiusVendorPairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class AdminControllerTest {

    AdminController controller;

    RadiusVendorPairService pairMock;

    DeliveryService deliveryMock;

    AuthorisationService authMock;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        pairMock = Mockito.mock(RadiusVendorPairService.class);
        deliveryMock = Mockito.mock(DeliveryService.class);
        authMock = Mockito.mock(AuthorisationService.class);
        controller = new AdminController(pairMock, deliveryMock, authMock);

        when(authMock.isValid(4L)).thenReturn(true);
        when(authMock.isValid(null)).thenReturn(false);
        when(authMock.isValid(1L)).thenReturn(true);

        when(authMock.isAdmin(4L)).thenReturn(false);
        when(authMock.isAdmin(null)).thenReturn(false);
        when(authMock.isAdmin(1L)).thenReturn(true);
    }

    @Test
    public void getCurrentDefaultRadiusResponse403() {
        ResponseEntity<GetCurrentDefaultRadius200Response> response = controller.getCurrentDefaultRadius(4L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
        verify(pairMock, never()).getRadiusVendorPair(-1L);
    }

    @Test
    public void getCurrentDefaultRadiusResponse400() {
        ResponseEntity<GetCurrentDefaultRadius200Response> response = controller.getCurrentDefaultRadius(null);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        verify(pairMock, never()).getRadiusVendorPair(-1L);
    }

    @Test
    public void getCurrentDefaultRadiusResponse404() {
        when(pairMock.getRadiusVendorPair(-1L)).thenReturn(Optional.empty());
        ResponseEntity<GetCurrentDefaultRadius200Response> response = controller.getCurrentDefaultRadius(1L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getCurrentDefaultRadiusResponse200() {
        when(pairMock.getRadiusVendorPair(-1L)).thenReturn(Optional.of(new RadiusVendorPair()
                .radius(55)
                .vendorId(-1L)));
        ResponseEntity<GetCurrentDefaultRadius200Response> response = controller.getCurrentDefaultRadius(1L);
        GetCurrentDefaultRadius200Response responseValue = new GetCurrentDefaultRadius200Response().radius(55);
        assertEquals(response, ResponseEntity.of(Optional.of(responseValue)));
    }

    @Test
    public void setCurrentDefaultRadiusResponse403() {
        SetDefaultRadiusRadiusParameter update = new SetDefaultRadiusRadiusParameter().radius(65);
        ResponseEntity<SetDefaultRadiusRadiusParameter> response = controller.setDefaultRadius(4L, update);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
        verify(pairMock, never()).getRadiusVendorPair(-1L);
    }

    @Test
    public void setCurrentDefaultRadiusResponse400() {
        SetDefaultRadiusRadiusParameter update = new SetDefaultRadiusRadiusParameter().radius(65);
        ResponseEntity<SetDefaultRadiusRadiusParameter> response = controller.setDefaultRadius(null, update);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        verify(pairMock, never()).getRadiusVendorPair(-1L);
    }

    @Test
    public void setCurrentDefaultRadiusResponse404() {
        SetDefaultRadiusRadiusParameter update = new SetDefaultRadiusRadiusParameter().radius(65);
        when(pairMock.getRadiusVendorPair(-1L)).thenReturn(Optional.empty());
        ResponseEntity<SetDefaultRadiusRadiusParameter> response = controller.setDefaultRadius(1L, update);
        assertEquals(response, ResponseEntity.of(Optional.of(update)));
    }

    @Test
    public void setCurrentDefaultRadiusResponse200() {
        SetDefaultRadiusRadiusParameter update = new SetDefaultRadiusRadiusParameter().radius(65);

        RadiusVendorPair radiusMock = Mockito.mock(RadiusVendorPair.class);
        when(radiusMock.getRadius()).thenReturn(55);
        when(radiusMock.getVendorId()).thenReturn(-1L);

        when(pairMock.getRadiusVendorPair(-1L)).thenReturn(Optional.of(radiusMock));
        ResponseEntity<SetDefaultRadiusRadiusParameter> response = controller.setDefaultRadius(1L, update);
        SetDefaultRadiusRadiusParameter responseValue = new SetDefaultRadiusRadiusParameter().radius(65);
        assertEquals(response, ResponseEntity.of(Optional.of(responseValue)));

        verify(radiusMock, times(1)).setRadius(65);
        verify(pairMock, times(1)).saveRadiusVendorPair(radiusMock);
    }

    @Test
    public void multipleRadiusUpdates() {
        SetDefaultRadiusRadiusParameter update = new SetDefaultRadiusRadiusParameter().radius(65);
        when(pairMock.getRadiusVendorPair(-1L)).thenReturn(Optional.of(new RadiusVendorPair()
                .radius(55)
                .vendorId(-1L)));
        ResponseEntity<SetDefaultRadiusRadiusParameter> response = controller.setDefaultRadius(1L, update);
        SetDefaultRadiusRadiusParameter update2 = new SetDefaultRadiusRadiusParameter().radius(45);
        SetDefaultRadiusRadiusParameter update3 = new SetDefaultRadiusRadiusParameter().radius(75);
        response = controller.setDefaultRadius(1L, update2);
        response = controller.setDefaultRadius(1L, update3);
        SetDefaultRadiusRadiusParameter responseValue = new SetDefaultRadiusRadiusParameter().radius(75);
        assertEquals(response, ResponseEntity.of(Optional.of(responseValue)));
        verify(pairMock, times(3)).getRadiusVendorPair(-1L);
    }

    @Test
    public void getAnalyticsResponse403() {
        ResponseEntity<Analytics> response = controller.getAnalytics(4L);
        assertEquals(response, new ResponseEntity<>(HttpStatus.FORBIDDEN));
        verify(pairMock, never()).getRadiusVendorPair(-1L);
    }

    @Test
    public void getAnalyticsResponse400() {
        ResponseEntity<Analytics> response = controller.getAnalytics(null);
        assertEquals(response, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        verify(pairMock, never()).getRadiusVendorPair(-1L);
    }

    @Test
    public void getAnalyticsResponse200() {
        Delivery delivery1 = new Delivery()
                .deliveryId(12L)
                .delivered(true)
                .times(new Times()
                        .estimatedDeliveryTime(OffsetDateTime.parse("2023-11-24 15:05:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualDeliveryTime(OffsetDateTime.parse("2023-11-24 15:10:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .estimatedPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("traffic"))));

        Delivery delivery2 = new Delivery()
                .deliveryId(13L)
                .delivered(true)
                .times(new Times()
                        .estimatedDeliveryTime(OffsetDateTime.parse("2023-11-24 15:05:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualDeliveryTime(OffsetDateTime.parse("2023-11-24 15:08:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .estimatedPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("tire flat"))));

        Delivery delivery3 = new Delivery()
                .deliveryId(14L)
                .delivered(true)
                .times(new Times()
                        .estimatedDeliveryTime(OffsetDateTime.parse("2023-11-24 15:15:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualDeliveryTime(OffsetDateTime.parse("2023-11-24 15:15:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .estimatedPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualPickupTime(OffsetDateTime.parse("2023-11-24 15:10:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("food was not ready"))));

        when(deliveryMock.getAllDeliveredDeliveries()).thenReturn(List.of(delivery1, delivery2, delivery3));
        ResponseEntity<Analytics> response = controller.getAnalytics(1L);
        Analytics responseValue = new Analytics()
                .completedDeliveries(3)
                .avgDeliveryTime(8)
                .driverEfficiency(71)
                .issues(List.of("traffic", "tire flat", "food was not ready"));
        assertEquals(response, ResponseEntity.of(Optional.of(responseValue)));
    }

    @Test
    public void getAnalyticsIncompleteTimesResponse200() {
        Delivery delivery1 = new Delivery()
                .deliveryId(12L)
                .delivered(true)
                .times(new Times()
                        .estimatedDeliveryTime(OffsetDateTime.parse("2023-11-24 15:05:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .estimatedPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("traffic"))));

        Delivery delivery2 = new Delivery()
                .deliveryId(13L)
                .delivered(true)
                .times(new Times()
                        .actualDeliveryTime(OffsetDateTime.parse("2023-11-24 15:08:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .estimatedPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("tire flat"))));

        Delivery delivery3 = new Delivery()
                .deliveryId(14L)
                .delivered(true)
                .times(new Times()
                        .estimatedDeliveryTime(OffsetDateTime.parse("2023-11-24 15:15:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualDeliveryTime(OffsetDateTime.parse("2023-11-24 15:15:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualPickupTime(OffsetDateTime.parse("2023-11-24 15:10:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("food was not ready"))));

        Delivery delivery4 = new Delivery()
                .deliveryId(14L)
                .delivered(true)
                .times(new Times()
                        .estimatedDeliveryTime(OffsetDateTime.parse("2023-11-24 15:15:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .actualDeliveryTime(OffsetDateTime.parse("2023-11-24 15:15:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .estimatedPickupTime(OffsetDateTime.parse("2023-11-24 15:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)))
                        .delays(List.of(new Delay().description("food was not ready"))));

        when(deliveryMock.getAllDeliveredDeliveries()).thenReturn(List.of(delivery1, delivery2, delivery3, delivery4));
        ResponseEntity<Analytics> response = controller.getAnalytics(1L);
        Analytics responseValue = new Analytics()
                .completedDeliveries(4)
                .avgDeliveryTime(0)
                .driverEfficiency(0)
                .issues(List.of("traffic", "tire flat", "food was not ready", "food was not ready"));
        assertEquals(response, ResponseEntity.of(Optional.of(responseValue)));
    }

}