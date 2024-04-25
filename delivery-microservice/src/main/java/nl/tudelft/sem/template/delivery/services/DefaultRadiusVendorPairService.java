package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import delivery_microservice.model.RadiusVendorPair;
import nl.tudelft.sem.template.delivery.database.RadiusVendorPairRepository;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultRadiusVendorPairService implements RadiusVendorPairService {
    private final RadiusVendorPairRepository radiusVendorPairRepository;
    private final GeocodingService geoCodingService;
    private final LocationRoutingService locationRoutingService;

    /**
     * Injects RadiusVendorPairRepository.
     *
     * @param radiusVendorPairRepository RadiusVendorPairRepository to be used
     */
    public DefaultRadiusVendorPairService(RadiusVendorPairRepository radiusVendorPairRepository,
                                          GeocodingService geoCodingService,
                                          LocationRoutingService locationRoutingService) {
        this.radiusVendorPairRepository = radiusVendorPairRepository;
        this.geoCodingService = geoCodingService;
        this.locationRoutingService = locationRoutingService;
    }

    @Override
    public boolean exists(long vendorId) {
        return radiusVendorPairRepository.existsById(vendorId);
    }

    @Override
    public Optional<RadiusVendorPair> getRadiusVendorPair(final Long vendorId) {
        Optional<RadiusVendorPair> radius = radiusVendorPairRepository.findById(vendorId);
        if (radius.isPresent()) {
            return radius;
        }

        // Couldn't find the RadiusVendorPair so get the default one
        Optional<RadiusVendorPair> defaultRadius = radiusVendorPairRepository.findById(-1L);
        if (defaultRadius.isPresent()) {
            return defaultRadius;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public RadiusVendorPair saveRadiusVendorPair(final RadiusVendorPair radiusVendorPair) {
        return radiusVendorPairRepository.save(radiusVendorPair);
    }

    /**
     * Gets all radius vendor pairs.
     *
     * @return list of radius vendor pairs
     */
    @Override
    public List<RadiusVendorPair> getAllRadiusVendorPairs() {
        return radiusVendorPairRepository.findAll();
    }

    /**
     * Checks for a given customer location whether it is in range of vendor based on it's maximum delivery radius.
     * If the vendor did not specify a radius, the default radius will be used.
     *
     * @param vendorId Long to identify the vendor and retrieve an optional custom delivery radius
     * @param customerLocation Location of customer to calculate distance to vendor
     * @param vendorLocation Location of vendor to calculate distance to customer
     * @return boolean whether the delivery is valid
     * @throws LocationNotFoundException is thrown if the GeocodingService cannot find the address
     */
    @Override
    public boolean vendorCustomerLocationIsValid(long vendorId, Location customerLocation, Location vendorLocation)
        throws LocationNotFoundException {
        // If no default radius is set this is the default
        int radius = 5000;

        Optional<RadiusVendorPair> radiusVendorPairOptional = getRadiusVendorPair(vendorId);
        if (radiusVendorPairOptional.isPresent()) {
            radius = radiusVendorPairOptional.get().getRadius();
        }

        Coordinate vendorCoordinate = geoCodingService.getCoordinatesFromLocation(vendorLocation);
        Coordinate customerCoordinate = geoCodingService.getCoordinatesFromLocation(customerLocation);

        int distance = locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate);

        return (distance <= radius);
    }

}
