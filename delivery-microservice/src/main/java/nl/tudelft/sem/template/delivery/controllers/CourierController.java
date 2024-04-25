package nl.tudelft.sem.template.delivery.controllers;

import delivery_microservice.api.CourierApi;
import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.CourierLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourierController implements CourierApi {

    private final CourierLocationService gpsService;

    private final AuthorisationService authorisationService;

    /**
     * Constructor.
     *
     * @param gpsService the gps location service
     * @param authService the authorization service
     */
    public CourierController(CourierLocationService gpsService, AuthorisationService authService) {
        this.gpsService = gpsService;
        this.authorisationService = authService;
    }

    /**
     * GET /courier/{courierId}/getLocation : Get courier location
     * Get real time location of courier. This will be fetched from a GPS service
     *
     * @param userId    (required)
     * @param courierId (required)
     * @return OK (status code 200)
     *         Bad Request (status code 400)
     *         Forbidden (status code 403)
     *         Not Found (status code 404)
     */
    @Override
    public ResponseEntity<Location> getCourierLocation(Long userId, Long courierId) {
        if (!authorisationService.isValid(userId) || !authorisationService.isValid(courierId)
                || !authorisationService.isCourier(courierId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!authorisationService.isUser(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            return ResponseEntity.ok(gpsService.getLocationOfCourier(courierId));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
