package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;

public interface CourierLocationService {
    /**
     * Fetch the current location of a (private) courier.
     *
     * @param courierId The id of the courier to fetch the location from
     * @return The current location of the courier
     * @throws EntityNotFoundException When the courier does not exist or their location could not be retrieved
     */
    Location getLocationOfCourier(long courierId) throws EntityNotFoundException;
}
