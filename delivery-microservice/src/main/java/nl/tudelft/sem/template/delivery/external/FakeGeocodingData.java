package nl.tudelft.sem.template.delivery.external;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.services.Coordinate;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;

import java.util.HashMap;

public class FakeGeocodingData extends HashMap<ImmutableLocation, Coordinate> {
    public void addLocation(Location location, Coordinate coordinate) {
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        put(immutableLocation, coordinate);
    }
}
