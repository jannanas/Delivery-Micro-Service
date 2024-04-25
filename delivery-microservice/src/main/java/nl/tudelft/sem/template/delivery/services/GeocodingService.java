package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;

public interface GeocodingService {
    /**
     * Get the coordinates of an address.
     *
     * @param location The address to lookup
     * @return The retrieved coordinates
     * @throws LocationNotFoundException When the address could not be found in the geocoding database
     */
    Coordinate getCoordinatesFromLocation(Location location) throws LocationNotFoundException;
}
