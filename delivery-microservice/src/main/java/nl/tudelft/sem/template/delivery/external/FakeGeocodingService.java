package nl.tudelft.sem.template.delivery.external;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.external.FakeGeocodingData;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;
import nl.tudelft.sem.template.delivery.services.Coordinate;
import nl.tudelft.sem.template.delivery.services.GeocodingService;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FakeGeocodingService implements GeocodingService {
    private final HashMap<ImmutableLocation, Coordinate> geocodingDatabase = new HashMap<>();

    public FakeGeocodingService(FakeGeocodingData fakeData) {
        geocodingDatabase.putAll(fakeData);
    }

    /**
     * Add a location to the geocoding lookup table. This method can be used this method for testing.
     *
     * @param location The location
     * @param coordinate The coordinate at the given location, or null if you want to remove the location
     */
    public void setLocationCoordinates(Location location, Coordinate coordinate) {
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        if (coordinate == null) {
            geocodingDatabase.remove(immutableLocation);
            return;
        }

        geocodingDatabase.put(immutableLocation, coordinate);
    }

    @Override
    public Coordinate getCoordinatesFromLocation(Location location) throws LocationNotFoundException {
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);

        if (!geocodingDatabase.containsKey(immutableLocation)) {
            throw new LocationNotFoundException(location);
        }

        return geocodingDatabase.get(immutableLocation);
    }
}
