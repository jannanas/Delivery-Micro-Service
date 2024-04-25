package nl.tudelft.sem.template.delivery.external;

import delivery_microservice.model.Location;
import delivery_microservice.model.PrivateCourier;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import nl.tudelft.sem.template.delivery.services.CourierLocationService;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FakeCourierLocationService implements CourierLocationService {
    private final HashMap<Long, ImmutableLocation> locationByCourier = new HashMap<>();

    public FakeCourierLocationService(FakeCourierLocationData fakeData) {
        locationByCourier.putAll(fakeData);
    }

    /**
     * Set the current location of a courier. This method can be used this method for testing.
     *
     * @param courierId The id of the courier
     * @param location The location where the courier currently is, or null if you want to remove the courier
     */
    public void setLocationOfCourier(long courierId, Location location) {
        if (location == null) {
            locationByCourier.remove(courierId);
            return;
        }

        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        locationByCourier.put(courierId, immutableLocation);
    }

    @Override
    public Location getLocationOfCourier(long courierId) throws EntityNotFoundException {
        if (!locationByCourier.containsKey(courierId)) {
            throw new EntityNotFoundException(PrivateCourier.class, courierId);
        }

        return locationByCourier.get(courierId).createMutableLocation();
    }
}
