package nl.tudelft.sem.template.delivery.controllers;

import delivery_microservice.api.AdminApi;
import delivery_microservice.model.Delivery;
import delivery_microservice.model.Delay;
import delivery_microservice.model.Analytics;
import delivery_microservice.model.GetCurrentDefaultRadius200Response;
import delivery_microservice.model.SetDefaultRadiusRadiusParameter;
import delivery_microservice.model.RadiusVendorPair;
import nl.tudelft.sem.template.delivery.services.RadiusVendorPairService;
import nl.tudelft.sem.template.delivery.services.DeliveryService;
import nl.tudelft.sem.template.delivery.services.AuthorisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AdminController implements AdminApi {

    private final RadiusVendorPairService defaultRadiusService;

    private final DeliveryService deliveryService;

    private final AuthorisationService authorisationService;

    /**
     * Constructor.
     *
     * @param service a service that would allow to get the default radius
     * @param deliveryService service that would allow to get the deliveries to create analytics
     */
    public AdminController(RadiusVendorPairService service, DeliveryService deliveryService,
                           AuthorisationService authService) {
        this.defaultRadiusService = service;
        this.deliveryService = deliveryService;
        this.authorisationService = authService;
    }

    /**
     * GET /admin/analytics : Get analytics.
     * Fetches the analytics for the admins
     *
     * @param userId (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<Analytics> getAnalytics(Long userId) {
        if (!authorisationService.isValid(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!authorisationService.isAdmin(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Delivery> deliveryList = deliveryService.getAllDeliveredDeliveries();
        long completedDelivery = deliveryList.stream().count();

        double averageDeliveryTimes = (double) deliveryList.stream()
                .filter(x -> x.getTimes().getActualPickupTime() != null)
                .filter(x -> x.getTimes().getActualDeliveryTime() != null)
                .filter(x -> x.getTimes().getEstimatedDeliveryTime() != null)
                .filter(x -> x.getTimes().getEstimatedPickupTime() != null)
                .map(Delivery::getTimes)
                .map(x -> Duration.between(x.getActualPickupTime(), x.getActualDeliveryTime())
                        .toMinutes())
                .collect(Collectors.summingLong(Long::longValue)) / ((double) completedDelivery);

        double driverEfficiency = (double) deliveryList.stream()
                .filter(x -> x.getTimes().getActualPickupTime() != null)
                .filter(x -> x.getTimes().getActualDeliveryTime() != null)
                .filter(x -> x.getTimes().getEstimatedDeliveryTime() != null)
                .filter(x -> x.getTimes().getEstimatedPickupTime() != null)
                .map(Delivery::getTimes)
                .map(x -> ((double) Duration.between(x.getEstimatedPickupTime(), x.getEstimatedDeliveryTime()).toMinutes())
                        / Duration.between(x.getActualPickupTime(), x.getActualDeliveryTime()).toMinutes())
                .map(value -> Math.min(value, 1.0))
                .collect(Collectors.summingDouble(Double::doubleValue)) * 100 / (double) completedDelivery;

        List<String> issueList = deliveryList.stream()
                .map(Delivery::getTimes)
                .flatMap(times -> times.getDelays().stream())
                .map(Delay::getDescription)
                .collect(Collectors.toList());

        Analytics analytics = new Analytics()
                .avgDeliveryTime(Long.valueOf(Math.round(averageDeliveryTimes)).intValue())
                .completedDeliveries(Long.valueOf(completedDelivery).intValue())
                .driverEfficiency((Long.valueOf(Math.round(driverEfficiency)).intValue()))
                .issues(issueList);

        return ResponseEntity.ok(analytics);
    }

    /**
     * GET /admin/default-radius : Get default delivery radius.
     * Get the app-wide default delivery radius. This is used if the vendor does not have a custom delivery radius
     *
     * @param userId (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<GetCurrentDefaultRadius200Response> getCurrentDefaultRadius(Long userId) {
        if (!authorisationService.isValid(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!authorisationService.isAdmin(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            GetCurrentDefaultRadius200Response radius
                    = new GetCurrentDefaultRadius200Response()
                    .radius(defaultRadiusService.getRadiusVendorPair(-1L).get().getRadius());
            return ResponseEntity.ok(radius);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /admin/default-radius : Set default radius.
     * Updates the default radius of all vendors without a specified radius
     *
     * @param userId (required)
     * @param radius (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @Override
    public ResponseEntity<SetDefaultRadiusRadiusParameter>
                    setDefaultRadius(Long userId, SetDefaultRadiusRadiusParameter radius) {
        if (!authorisationService.isValid(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!authorisationService.isAdmin(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Optional<RadiusVendorPair> vendorPairService = defaultRadiusService.getRadiusVendorPair(-1L);
            vendorPairService.get().setRadius(radius.getRadius());
            defaultRadiusService.saveRadiusVendorPair(vendorPairService.get());
            return ResponseEntity.ok(radius);
        } catch (RuntimeException e) {
            RadiusVendorPair pair = new RadiusVendorPair()
                    .radius(radius.getRadius())
                    .vendorId(-1L);
            defaultRadiusService.saveRadiusVendorPair(pair);
            return ResponseEntity.ok(radius);
        }
    }
}
