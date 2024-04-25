package nl.tudelft.sem.template.delivery.controllers;


import delivery_microservice.api.DeliveryApi;
import delivery_microservice.model.CreateDeliveryRequest;
import delivery_microservice.model.Delivery;
import delivery_microservice.model.Location;
import delivery_microservice.model.UpdateDeliveryRequest;
import nl.tudelft.sem.template.delivery.dtos.UpdateDeliveryDto;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    private final AuthorisationService authorisationService;


    /**
     * Constructor.
     *
     * @param deliveryService      The interface with the needed methods
     * @param authorisationService The verification of the user
     */
    public DeliveryController(DeliveryService deliveryService, AuthorisationService authorisationService) {
        this.deliveryService = deliveryService;
        this.authorisationService = authorisationService;
    }

    /**
     * Verifies if any of the userIds is invalid.
     *
     * @param ids the list of id
     * @return true or false
     */
    private boolean isInvalidId(Long... ids) {
        for (Long id : ids) {
            if (id == null || id < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if every userId is invalid.
     *
     * @param ids the ids
     * @return true or false
     */
    private boolean isInvalidIds(Long... ids) {
        for (Long id : ids) {
            if (id != null && id >= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies if a location is valid.
     *
     * @param customerLocation the location
     * @return true or false
     */
    private boolean isInvalidLocation(Location customerLocation) {
        return customerLocation == null;
    }

    /**
     * Method used as a helper to decrease the cyclomatic complexity.
     *
     * @param userId id of the user
     * @param request the request
     * @return the returned delivery
     */
    private ResponseEntity<Delivery> helperCreate(Long userId, CreateDeliveryRequest request) {
        try {
            Delivery newDelivery = deliveryService.createDelivery(request.getOrderId(), request.getVendorId(),
                request.getDeliveryLocation(), request.getEstimatedPickupTime());
            if (newDelivery == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Delivery());
            }
            return ResponseEntity.ok(newDelivery);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /delivery : Create delivery.
     * Creates a new delivery object for a provided order
     *
     * @param userId                (required)
     * @param request (required)
     * @return OK (status code 200)
     *          or Bad Request (status code 400)
     *          or Forbidden (status code 403)
     *          or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<Delivery> createDelivery(Long userId, CreateDeliveryRequest request) {
        if (isInvalidId(request.getOrderId(), request.getVendorId()) || isInvalidLocation(request.getDeliveryLocation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Delivery());
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return helperCreate(userId, request);
    }

    /**
     * GET /delivery/{deliveryId} : Get delivery.
     * Fetches delivery object based on delivery id
     *
     * @param userId     (required)
     * @param deliveryId (required)
     * @return OK (status code 200)
     *          or Bad Request (status code 400)
     *          or Forbidden (status code 403)
     *          or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<Delivery> getDeliveryById(Long userId, Long deliveryId) {
        {
            if (isInvalidId(userId, deliveryId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Delivery());
            }
            if (!authorisationService.isUser(userId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            Optional<Delivery> optionalDelivery = deliveryService.getDelivery(deliveryId);
            if (optionalDelivery.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Delivery());
            }
            return ResponseEntity.ok(optionalDelivery.get());
        }
    }

    /**
     * GET /delivery : Get delivery.
     * Fetches delivery based on the order id
     *
     * @param userId  (required)
     * @param orderId (required)
     * @return OK (status code 200)
     *          or Bad Request (status code 400)
     *          or Forbidden (status code 403)
     *          or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<List<Delivery>> getDeliveryFromOrder(Long userId, Long orderId, Long vendorId, Long courierId) {
        List<Delivery> delivery = new ArrayList<>();
        if (isInvalidId(userId) || isInvalidIds(orderId, courierId, vendorId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(new Delivery()));
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (isInvalidIds(orderId, vendorId)) {
            delivery = deliveryService.getDeliveryByCourierId(courierId);
            if (delivery.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(new Delivery()));
            }
        }
        if (isInvalidIds(orderId, courierId)) {
            delivery = deliveryService.getDeliveryByVendorId(vendorId);
            if (delivery.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(new Delivery()));
            }
        }
        if (isInvalidIds(vendorId, courierId)) {
            Optional<Delivery> byOrder = deliveryService.getDeliveryByOrderId(orderId);
            if (byOrder.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(new Delivery()));
            }
            return ResponseEntity.ok(List.of(byOrder.get()));
        }
        return ResponseEntity.ok(delivery);
    }

    /**
     * GET /delivery/unassigned : Gets all ids of deliveries without a courier.
     * Fetches all deliveries without a courier, this allows for couriers to select an order or get the next order
     *
     * @param userId (required)
     * @return OK (status code 200)
     *          or Bad Request (status code 400)
     *          or Forbidden (status code 403)
     *          or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<List<Long>> getUnassignedDeliveries(Long userId) {
        if (isInvalidId(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<Long>());
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Long> unnassignedDelivery = deliveryService.getUnassigned();
        if (unnassignedDelivery.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<Long>());
        }
        return ResponseEntity.ok(unnassignedDelivery);
    }

    /**
     * Method used as a helper to decrease the cyclomatic complexity.
     *
     * @param userId id of the user
     * @param deliveryId id of the updated delivery
     * @param request the update request
     * @return the delivery object
     */

    private ResponseEntity<Delivery> helperUpdate(Long userId, Long deliveryId,
                                                  UpdateDeliveryRequest request) {
        try {
            UpdateDeliveryDto dto = UpdateDeliveryDto.builder()
                .deliveryId(deliveryId).courierId(request.getCourierId()).rating(request.getRating())
                    .times(request.getTimes()).status(request.getStatus()).delivered(request.getDelivered()).build();
            Delivery updatedDelivery = deliveryService.updateDelivery(userId, dto);
            if (updatedDelivery == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Delivery());
            }
            return ResponseEntity.ok(updatedDelivery);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /delivery/{deliveryId} : Update delivery.
     * Updates delivery based on the delivery id. It can update the courier, the rating, the delivery times,
     * the status or if it has been delivered. None of the fields are mandatory. Update is only on fields provided
     *
     * @param userId                (required)
     * @param deliveryId            (required)
     * @param request (required)
     * @return OK (status code 200)
     *          or Bad Request (status code 400)
     *          or Forbidden (status code 403)
     *          or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<Delivery> updateDelivery(Long userId, Long deliveryId,
                                                   UpdateDeliveryRequest request) {
        if (isInvalidId(userId, deliveryId) || request == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Delivery());
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return helperUpdate(userId, deliveryId, request);
    }
}
