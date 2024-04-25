package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import delivery_microservice.model.RadiusVendorPair;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;

import java.util.List;
import java.util.Optional;

public interface RadiusVendorPairService {

    /**
     * Check if relation exists.
     *
     * @param vendorId Long that identifies vendor
     * @return boolean
     */
    boolean exists(long vendorId);

    /**
     * Finds a specific RadiusVendorPair based on unique id.
     * Checks if there is a custom radius, otherwise returns default radius
     *
     * @param vendorId Long vendor id
     * @return None or radius
     */
    Optional<RadiusVendorPair> getRadiusVendorPair(final Long vendorId);

    /**
     * Saves private courier by replacing existing entity or creating new .
     *
     * @param radiusVendorPair PrivateCourier to be saved
     * @return Saved RadiusVendorPair
     */
    RadiusVendorPair saveRadiusVendorPair(final RadiusVendorPair radiusVendorPair);

    /**
     * Gets all radius vendor pairs.
     *
     * @return list of radius vendor pairs
     */
    List<RadiusVendorPair> getAllRadiusVendorPairs();

    /**
     * Checks for a given customer location whether it is in range of vendor based on it's maximum delivery radius.
     * If the vendor did not specify a radius, the default radius will be used.
     * Checks for a given delivery whether the distance between vendor and customer is less than the
     * maximum radius that business is willing to deliver to. If the business set a custom radius that should
     * be used, but if none was set the default radius will be used.
     *
     * @param vendorId         Long to identify the vendor and retrieve an optional custom delivery radius
     * @param customerLocation Location of customer to calculate distance to vendor
     * @param vendorLocation   Location of vendor to calculate distance to customer
     * @return boolean whether the delivery is valid
     * @throws LocationNotFoundException is thrown if the GeocodingService cannot find the address
     */
    boolean vendorCustomerLocationIsValid(long vendorId, Location customerLocation, Location vendorLocation)
        throws LocationNotFoundException;
}

