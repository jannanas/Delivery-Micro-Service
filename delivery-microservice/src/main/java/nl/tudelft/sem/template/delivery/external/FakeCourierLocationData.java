package nl.tudelft.sem.template.delivery.external;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;

import java.util.HashMap;

public class FakeCourierLocationData extends HashMap<Long, ImmutableLocation> {
    public void addCourierLocation(long courierId, Location location) {
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        put(courierId, immutableLocation);
    }
}
