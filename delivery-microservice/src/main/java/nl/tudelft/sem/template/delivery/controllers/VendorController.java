package nl.tudelft.sem.template.delivery.controllers;

import delivery_microservice.api.VendorApi;
import delivery_microservice.model.GetDeliveryRadii200ResponseInner;
import delivery_microservice.model.GetVendorDeliveryRadius200Response;
import delivery_microservice.model.IsCustomerInRangeFromVendor200Response;
import delivery_microservice.model.Location;
import delivery_microservice.model.PrivateCourier;
import delivery_microservice.model.RadiusVendorPair;
import delivery_microservice.model.UpdateVendorDeliveryRadius200Response;
import delivery_microservice.model.UpdateVendorDeliveryRadiusRequest;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import nl.tudelft.sem.template.delivery.services.CourierService;
import nl.tudelft.sem.template.delivery.services.RadiusVendorPairService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VendorController implements VendorApi {

    private final RadiusVendorPairService radiusService;
    private final CourierService courierService;

    private final AuthorisationService authorisationService;

    /**
     * Constructor.
     *
     * @param service              A RadiusVendorPairService
     * @param courierService       A CourierService
     * @param authorisationService An AuthorisationService
     */
    public VendorController(RadiusVendorPairService service, CourierService courierService,
                            AuthorisationService authorisationService) {
        this.radiusService = service;
        this.courierService = courierService;
        this.authorisationService = authorisationService;
    }

    /**
     * GET /vendor/{vendorId}/couriers : Get vendor specific couriers
     * Get all couriers that are hired by the provided vendor.
     *
     * @param userId   (required)
     * @param vendorId (required)
     * @return OK (status code 200)
     *     or Bad Request (status code 400)
     *     or Forbidden (status code 403)
     *     or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<List<PrivateCourier>> getCouriersByVendor(Long userId, Long vendorId) {
        if (vendorId == null || userId == null) {
            return ResponseEntity.status(400).build();
        }
        if (!authorisationService.isUser(userId)) {
            return ResponseEntity.status(403).build();
        }
        if (!authorisationService.isVendor(vendorId)) {
            return ResponseEntity.status(404).build();
        }

        var couriers = courierService.getAllPrivateCouriersByVendor(vendorId);
        return ResponseEntity.ok(couriers);
    }

    /**
     * GET /vendor/delivery-radii : Get all delivery radii
     * Gets all delivery radii that are set by the vendors.
     * This will only return a list of vendors that have a custom delivery radius.
     * So vendors that have not set a custom delivery radius, or set their radius to &#x60;0&#x60; will not be returned.
     *
     * @param userId (required)
     * @return OK (status code 200)
     *     or Bad Request (status code 400)
     *     or Forbidden (status code 403)
     *     or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<List<GetDeliveryRadii200ResponseInner>> getDeliveryRadii(Long userId) {
        if (!authorisationService.isUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var list = radiusService.getAllRadiusVendorPairs().stream()
            .map(e -> new GetDeliveryRadii200ResponseInner().vendorID(e.getVendorId()).radius(e.getRadius()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    /**
     * GET /vendor/{vendorId}/delivery-radius : Get vendor delivery radius
     * Get the delivery radius for the vendor. If the radius is set to 0 or not set, the default radius will be used
     *
     * @param userId   (required)
     * @param vendorId (required)
     * @return OK (status code 200)
     *     or Bad Request (status code 400)
     *     or Forbidden (status code 403)
     *     or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<GetVendorDeliveryRadius200Response> getVendorDeliveryRadius(Long userId, Long vendorId) {
        if (!authorisationService.isUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var radius = radiusService.getRadiusVendorPair(vendorId);

        return radius.map(radiusVendorPair -> ResponseEntity.ok(
                new GetVendorDeliveryRadius200Response().radius(radiusVendorPair.getRadius())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    /**
     * GET /vendor/isInRange : Is customer in range from vendor
     * Returns a valid response if a given customer address is in range from the specified vendor according to the vendors
     * delivery radius.
     *
     * @param userId           (required)
     * @param vendorId         (required)
     * @param customerLocation (required)
     * @return OK (status code 200)
     *     or Bad Request (status code 400)
     *     or Forbidden (status code 403)
     *     or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<IsCustomerInRangeFromVendor200Response> isCustomerInRangeFromVendor(Long userId, Long vendorId,
                                                                  Location customerLocation, Location vendorLocation) {
        if (!authorisationService.isUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!authorisationService.isVendor(vendorId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (customerLocation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean inRange = false;
        try {
            inRange = radiusService.vendorCustomerLocationIsValid(vendorId, customerLocation, vendorLocation);
        } catch (LocationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(new IsCustomerInRangeFromVendor200Response().isInRange(inRange));
    }

    /**
     * PUT /vendor/{vendorId}/delivery-radius : Update vendor delivery radius
     * This sets the delivery radius for the vendor. If the radius is set to 0, the default radius will be used.
     *
     * @param userId                            (required)
     * @param vendorId                          (required)
     * @param updateVendorDeliveryRadiusRequest (required)
     * @return OK (status code 200)
     *     or Bad Request (status code 400)
     *     or Forbidden (status code 403)
     *     or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<UpdateVendorDeliveryRadius200Response> updateVendorDeliveryRadius(Long userId, Long vendorId,
                                                    UpdateVendorDeliveryRadiusRequest updateVendorDeliveryRadiusRequest) {
        if (updateVendorDeliveryRadiusRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!authorisationService.isVendor(vendorId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!authorisationService.isVendor(userId) || !userId.equals(vendorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var radius = updateVendorDeliveryRadiusRequest.getRadius();
        var pair = new RadiusVendorPair().vendorId(vendorId).radius(radius);

        var newRadius = radiusService.saveRadiusVendorPair(pair);
        return ResponseEntity.ok(new UpdateVendorDeliveryRadius200Response().radius(newRadius.getRadius()));
    }
}
