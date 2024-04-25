package nl.tudelft.sem.template.example.integration;

import delivery_microservice.model.CreateDeliveryRequest;
import delivery_microservice.model.Delivery;
import delivery_microservice.model.Location;
import delivery_microservice.model.Times;
import delivery_microservice.model.UpdateDeliveryRequest;
import nl.tudelft.sem.template.delivery.controllers.DeliveryController;
import nl.tudelft.sem.template.delivery.dtos.UpdateDeliveryDto;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.DeliveryService;
import orders_microservice.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeliveryControllerTest {
    DeliveryService deliveryServiceMock;

    DeliveryController deliveryControllerMock;

    AuthorisationService authorisationService;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        deliveryServiceMock = mock(DeliveryService.class);
        authorisationService = mock(AuthorisationService.class);
        deliveryControllerMock = new DeliveryController(deliveryServiceMock, authorisationService);

        when(authorisationService.isUser(1L)).thenReturn(true);
        when(authorisationService.isUser(2L)).thenReturn(true);
        when(authorisationService.isUser(0L)).thenReturn(true);
        when(authorisationService.isUser(9L)).thenReturn(false);
    }

    @Test
    void createDeliverySuccess() throws EntityNotFoundException, users_microservice.ApiException {
        Delivery example = new Delivery();
        Long userId = 0L;
        Location loc = new Location();

        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest(1L, 2L, loc);

        when(deliveryServiceMock.createDelivery(eq(1L), eq(2L), eq(loc), any())).thenReturn(example);

        Delivery created = deliveryControllerMock.createDelivery(userId, createDeliveryRequest).getBody();
        assertEquals(example, created);
    }

    @Test
    public void testCreateDeliveryBadRequest() throws EntityNotFoundException, users_microservice.ApiException {
        Long userId = -1L;
        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest(1L, 2L, null);

        ResponseEntity<Delivery> example = deliveryControllerMock.createDelivery(userId, createDeliveryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, never()).createDelivery(anyLong(), anyLong(), any(), any());
    }

    @Test
    public void testCreateDeliveryForbidden() throws EntityNotFoundException, users_microservice.ApiException {
        Long userId = 9L;
        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest(1L, 2L, new Location());

        ResponseEntity<Delivery> example = deliveryControllerMock.createDelivery(userId, createDeliveryRequest);

        assertEquals(HttpStatus.FORBIDDEN, example.getStatusCode());
        verify(deliveryServiceMock, never()).createDelivery(anyLong(), anyLong(), any(), any());
    }

    @Test
    public void testCreateDeliveryNotFound() throws EntityNotFoundException, users_microservice.ApiException {
        Long userId = 1L;
        Location loc = new Location();
        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest(1L, 2L, loc);
        when(deliveryServiceMock.createDelivery(eq(1L), eq(2L), eq(loc), any()))
            .thenReturn(null);

        ResponseEntity<Delivery> example = deliveryControllerMock.createDelivery(userId, createDeliveryRequest);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).createDelivery(eq(1L), eq(2L), eq(loc), any());
    }

    @Test
    public void testCreateDeliveryCatch() throws EntityNotFoundException, users_microservice.ApiException {
        Long userId = 1L;
        Location loc = new Location();
        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest(1L, 2L, loc);
        RuntimeException mockException = mock(RuntimeException.class);

        when(deliveryServiceMock.createDelivery(eq(1L), eq(2L), eq(loc), any()))
            .thenThrow(mockException);

        ResponseEntity<Delivery> example = deliveryControllerMock.createDelivery(userId, createDeliveryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).createDelivery(eq(1L), eq(2L), eq(loc), any());
        verify(mockException, times(1)).printStackTrace();
    }

    @Test
    public void testUnassignedBadRequest() {
        Long userId = -1L;
        ResponseEntity<List<Long>> example = deliveryControllerMock.getUnassignedDeliveries(userId);

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, never()).getUnassigned();
    }

    @Test
    public void testUnassignedForbidden() {
        Long userId = 9L;
        ResponseEntity<List<Long>> example = deliveryControllerMock.getUnassignedDeliveries(userId);

        assertEquals(HttpStatus.FORBIDDEN, example.getStatusCode());
        verify(deliveryServiceMock, never()).getUnassigned();
    }

    @Test
    public void testUnassignedSuccess() {
        Long userId = 1L;

        List<Long> expected = new ArrayList<>();
        expected.add(4L);

        when(deliveryServiceMock.getUnassigned()).thenReturn(expected);
        List<Long> example = deliveryControllerMock.getUnassignedDeliveries(userId).getBody();

        assertEquals(expected, example);
        verify(deliveryServiceMock, times(1)).getUnassigned();

    }

    @Test
    public void testUnassignedNotFound() {
        Long userId = 1L;

        when(deliveryServiceMock.getUnassigned()).thenReturn(new ArrayList<>());
        ResponseEntity<List<Long>> example = deliveryControllerMock.getUnassignedDeliveries(userId);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).getUnassigned();

    }

    @Test
    public void testByDeliveryIdBadRequest() {
        Long userId = -1L;
        Long deliveryId = 1L;
        ResponseEntity<Delivery> example = deliveryControllerMock.getDeliveryById(userId, deliveryId);

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, never()).getDelivery(deliveryId);
    }

    @Test
    public void testByDeliveryIdForbidden() {
        Long userId = 9L;
        Long deliveryId = 1L;
        ResponseEntity<Delivery> example = deliveryControllerMock.getDeliveryById(userId, deliveryId);

        assertEquals(HttpStatus.FORBIDDEN, example.getStatusCode());
        verify(deliveryServiceMock, never()).getDelivery(deliveryId);
    }

    @Test
    public void testByDeliveryIdNotFound() {
        Long userId = 1L;
        Long deliveryId = 1L;

        when(deliveryServiceMock.getDelivery(deliveryId)).thenReturn(Optional.empty());
        ResponseEntity<Delivery> example = deliveryControllerMock.getDeliveryById(userId, deliveryId);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).getDelivery(deliveryId);
    }

    @Test
    public void testByDeliveryIdSuccess() {
        Long userId = 0L;
        Long deliveryId = 0L;

        Delivery expected = new Delivery();

        when(deliveryServiceMock.getDelivery(deliveryId)).thenReturn(Optional.of(expected));
        Delivery example = deliveryControllerMock.getDeliveryById(userId, deliveryId).getBody();

        assertEquals(expected, example);
        verify(deliveryServiceMock, times(1)).getDelivery(deliveryId);
    }

    @Test
    public void testUpdateBadRequest() throws EntityNotFoundException, ApiException {
        Long userId = -1L;
        Long deliveryId = 2L;
        Long courierId = 6L;
        int rating = 4;
        Times time = new Times();
        boolean del = true;
        var status = UpdateDeliveryRequest.StatusEnum.ACCEPTED;
        UpdateDeliveryRequest updateDeliveryRequest = new UpdateDeliveryRequest();
        updateDeliveryRequest.setCourierId(courierId);
        updateDeliveryRequest.setRating(rating);
        updateDeliveryRequest.setTimes(time);
        updateDeliveryRequest.setStatus(status);
        updateDeliveryRequest.setDelivered(del);

        ResponseEntity<Delivery> example = deliveryControllerMock.updateDelivery(userId, deliveryId, updateDeliveryRequest);

        UpdateDeliveryDto dto =
            UpdateDeliveryDto.builder().deliveryId(deliveryId).courierId(courierId).rating(rating).times(time)
                .status(status).delivered(del).build();

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, never()).updateDelivery(1L, dto);
    }


    @Test
    public void testUpdateForbidden() throws EntityNotFoundException, ApiException {
        Long userId = 9L;
        Long deliveryId = 2L;
        Long courierId = 6L;
        int rating = 4;
        Times time = new Times();
        boolean del = true;
        var status = UpdateDeliveryRequest.StatusEnum.ACCEPTED;
        UpdateDeliveryRequest updateDeliveryRequest = new UpdateDeliveryRequest();
        updateDeliveryRequest.setCourierId(courierId);
        updateDeliveryRequest.setRating(rating);
        updateDeliveryRequest.setTimes(time);
        updateDeliveryRequest.setStatus(status);
        updateDeliveryRequest.setDelivered(del);

        ResponseEntity<Delivery> example = deliveryControllerMock.updateDelivery(userId, deliveryId, updateDeliveryRequest);

        UpdateDeliveryDto dto =
            UpdateDeliveryDto.builder().deliveryId(deliveryId).courierId(courierId).rating(rating).times(time)
                .status(status).delivered(del).build();

        assertEquals(HttpStatus.FORBIDDEN, example.getStatusCode());
        verify(deliveryServiceMock, never()).updateDelivery(9L, dto);
    }

    @Test
    public void testUpdateNotFoundRequest() throws EntityNotFoundException, ApiException {
        Long deliveryId = 2L;
        Long courierId = 6L;
        int rating = 4;
        Times time = new Times();
        boolean del = true;
        var status = UpdateDeliveryRequest.StatusEnum.ACCEPTED;
        UpdateDeliveryRequest updateDeliveryRequest = new UpdateDeliveryRequest();
        updateDeliveryRequest.setCourierId(courierId);
        updateDeliveryRequest.setRating(rating);
        updateDeliveryRequest.setTimes(time);
        updateDeliveryRequest.setStatus(status);
        updateDeliveryRequest.setDelivered(del);
        Long userId = 1L;
        UpdateDeliveryDto dto =
            UpdateDeliveryDto.builder().deliveryId(deliveryId).courierId(courierId).rating(rating).times(time)
                .status(status).delivered(del).build();

        when(deliveryServiceMock.updateDelivery(1L, dto)).thenReturn(null);

        ResponseEntity<Delivery> example = deliveryControllerMock.updateDelivery(userId, deliveryId, updateDeliveryRequest);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).updateDelivery(1L, dto);
    }

    @Test
    public void testUpdateSuccess() throws EntityNotFoundException, ApiException {
        Long deliveryId = 2L;
        Long courierId = 6L;
        Integer rating = null;
        Times time = new Times();
        boolean del = true;
        var status = UpdateDeliveryRequest.StatusEnum.ACCEPTED;
        UpdateDeliveryRequest updateDeliveryRequest = new UpdateDeliveryRequest();
        updateDeliveryRequest.setCourierId(courierId);
        updateDeliveryRequest.setRating(rating);
        updateDeliveryRequest.setTimes(time);
        updateDeliveryRequest.setStatus(status);
        updateDeliveryRequest.setDelivered(del);

        UpdateDeliveryDto dto =
            UpdateDeliveryDto.builder().deliveryId(deliveryId).courierId(courierId).rating(rating).times(time)
                .status(status).delivered(del).build();
        Long userId = 1L;
        Delivery expected = new Delivery();

        when(deliveryServiceMock.updateDelivery(1L, dto)).thenReturn(expected);

        Delivery example = deliveryControllerMock.updateDelivery(userId, deliveryId, updateDeliveryRequest).getBody();

        assertEquals(example, expected);
        verify(deliveryServiceMock, times(1)).updateDelivery(1L, dto);
    }

    @Test
    public void testUpdateCatch() throws EntityNotFoundException, ApiException {
        Long deliveryId = 2L;
        Long courierId = 6L;
        Integer rating = null;
        Times time = new Times();
        boolean del = true;
        var status = UpdateDeliveryRequest.StatusEnum.ACCEPTED;
        UpdateDeliveryRequest updateDeliveryRequest = new UpdateDeliveryRequest();
        updateDeliveryRequest.setCourierId(courierId);
        updateDeliveryRequest.setRating(rating);
        updateDeliveryRequest.setTimes(time);
        updateDeliveryRequest.setStatus(status);
        updateDeliveryRequest.setDelivered(del);

        RuntimeException mockException = mock(RuntimeException.class);

        UpdateDeliveryDto dto =
            UpdateDeliveryDto.builder().deliveryId(deliveryId).courierId(courierId).rating(rating).times(time)
                .status(status).delivered(del).build();

        Long userId = 1L;
        when(deliveryServiceMock.updateDelivery(1L, dto)).thenThrow(mockException);

        ResponseEntity<Delivery> example = deliveryControllerMock.updateDelivery(userId, deliveryId, updateDeliveryRequest);

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).updateDelivery(1L, dto);
        verify(mockException, times(1)).printStackTrace();
    }

    @Test
    public void testGetBadRequest() {
        Long userId = -2L;
        Long vendorId = 2L;
        Long orderId = 4L;
        Long courierId = 77L;
        ResponseEntity<List<Delivery>> example =
            deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId);

        assertEquals(HttpStatus.BAD_REQUEST, example.getStatusCode());
        verify(deliveryServiceMock, never()).getDeliveryByCourierId(courierId);
        verify(deliveryServiceMock, never()).getDeliveryByVendorId(vendorId);
        verify(deliveryServiceMock, never()).getDeliveryByOrderId(orderId);
    }

    @Test
    public void testGetForbidden() {
        Long userId = 9L;
        Long vendorId = 2L;
        Long orderId = 4L;
        Long courierId = 77L;
        ResponseEntity<List<Delivery>> example =
            deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId);

        assertEquals(HttpStatus.FORBIDDEN, example.getStatusCode());
        verify(deliveryServiceMock, never()).getDeliveryByCourierId(courierId);
        verify(deliveryServiceMock, never()).getDeliveryByVendorId(vendorId);
        verify(deliveryServiceMock, never()).getDeliveryByOrderId(orderId);
    }

    @Test
    public void testGetCourierRequest() {
        Long userId = 2L;
        Long vendorId = -1L;
        Long orderId = -4L;
        Long courierId = 0L;
        Delivery expected = new Delivery();
        List<Delivery> list = new ArrayList<>();
        list.add(expected);
        when(deliveryServiceMock.getDeliveryByCourierId(courierId)).thenReturn(list);

        List<Delivery> example = deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId).getBody();

        assertEquals(list, example);
        verify(deliveryServiceMock, times(1)).getDeliveryByCourierId(courierId);
    }

    @Test
    public void testGetCourierNotFoundRequest() {
        Long userId = 2L;
        Long vendorId = -1L;
        Long orderId = -4L;
        Long courierId = 77L;
        List<Delivery> list = new ArrayList<>();
        when(deliveryServiceMock.getDeliveryByCourierId(courierId)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Delivery>> example =
            deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).getDeliveryByCourierId(courierId);
    }

    @Test
    public void testGetVendorRequest() {
        Long userId = 2L;
        Long vendorId = 0L;
        Long orderId = -4L;
        Long courierId = -77L;
        Delivery expected = new Delivery();
        List<Delivery> list = new ArrayList<>();
        list.add(expected);
        when(deliveryServiceMock.getDeliveryByVendorId(vendorId)).thenReturn(list);

        List<Delivery> example = deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId).getBody();

        assertEquals(list, example);
        verify(deliveryServiceMock, times(1)).getDeliveryByVendorId(vendorId);
    }

    @Test
    public void testGetVendorNotFoundRequest() {
        Long userId = 2L;
        Long vendorId = 1L;
        Long orderId = -1L;
        Long courierId = -1L;

        when(deliveryServiceMock.getDeliveryByVendorId(vendorId)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Delivery>> example =
            deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).getDeliveryByVendorId(vendorId);
    }

    @Test
    public void testGetOrderRequest() {
        Long userId = 2L;
        Long vendorId = -1L;
        Long orderId = 0L;
        Long courierId = -77L;
        Delivery expected = new Delivery();

        when(deliveryServiceMock.getDeliveryByOrderId(orderId)).thenReturn(Optional.of(expected));

        List<Delivery> example = deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId).getBody();

        assertEquals(List.of(expected), example);
        verify(deliveryServiceMock, times(1)).getDeliveryByOrderId(orderId);
    }

    @Test
    public void testGetOrderNotFoundRequest() {
        Long userId = 2L;
        Long vendorId = -1L;
        Long orderId = 4L;
        Long courierId = -77L;
        when(deliveryServiceMock.getDeliveryByOrderId(orderId)).thenReturn(Optional.empty());

        ResponseEntity<List<Delivery>> example =
            deliveryControllerMock.getDeliveryFromOrder(userId, orderId, vendorId, courierId);

        assertEquals(HttpStatus.NOT_FOUND, example.getStatusCode());
        verify(deliveryServiceMock, times(1)).getDeliveryByOrderId(orderId);
    }

}

