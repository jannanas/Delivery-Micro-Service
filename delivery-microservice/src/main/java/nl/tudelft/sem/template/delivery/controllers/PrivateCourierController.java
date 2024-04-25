package nl.tudelft.sem.template.delivery.controllers;

import delivery_microservice.api.PrivateCourierApi;
import delivery_microservice.model.PrivateCourier;
import delivery_microservice.model.UpdateCourierRequest;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.CourierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PrivateCourierController implements PrivateCourierApi {

    private final CourierService courierService;

    private final AuthorisationService authorisationService;

    /**
     * Constructor.
     *
     * @param courierService The courier service
     * @param authService the authorization service
     */
    public PrivateCourierController(CourierService courierService, AuthorisationService authService) {
        this.courierService = courierService;
        this.authorisationService = authService;
    }

    /**
     * GET /private-courier/{courierId} : Get vendor for courier.
     * Get vendor-courier relation for the vendor that hired the provided courier
     *
     * @param userId    (required)
     * @param courierId (required)
     * @return OK (status code 200)
     *         Bad Request (status code 400)
     *         Forbidden (status code 403)
     *         Not Found (status code 404)
     */
    @Override
    public ResponseEntity<PrivateCourier> getCourier(Long userId, Long courierId) {
        if (!authorisationService.isValid(userId) || !authorisationService.isValid(courierId)
                || !authorisationService.isCourier(courierId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<PrivateCourier> courier = courierService.getPrivateCourier(courierId);
        if (courier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(courier.get());
    }

    /**
     * PUT /private-courier/{courierId} : Update vendor for courier.
     * This sets the vendor that hired the courier
     *
     * @param userId               (required)
     * @param courierId            (required)
     * @param updateCourierRequest (required)
     * @return OK (status code 200)
     *         Bad Request (status code 400)
     *         Forbidden (status code 403)
     *         Not Found (status code 404)
     */
    @Override
    public ResponseEntity<PrivateCourier> updateCourier(Long userId,
                                                        Long courierId, UpdateCourierRequest updateCourierRequest) {
        if (!authorisationService.isValid(userId) || !authorisationService.isValid(courierId)
                || !authorisationService.isCourier(courierId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<PrivateCourier> courier = courierService.getPrivateCourier(courierId);
        if (courier.isEmpty()) {
            PrivateCourier pv = new PrivateCourier()
                    .courierId(courierId)
                    .vendorId(updateCourierRequest.getVendorId());
            courierService.savePrivateCourier(pv);
            return ResponseEntity.ok(pv);
        }
        courier.get().setVendorId(updateCourierRequest.getVendorId());
        courierService.savePrivateCourier(courier.get());
        return ResponseEntity.ok(courier.get());
    }
}
