package nl.tudelft.sem.template.delivery.external;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.services.Coordinate;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeGeocodingDataTest {
    @Test
    void addLocation_test() {
        Location location = new Location().country("Netherlands");
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        Coordinate coordinate = new Coordinate(1, 2);

        FakeGeocodingData data = new FakeGeocodingData();
        data.addLocation(location, coordinate);

        assertThat(data.size()).isEqualTo(1);
        assertThat(data.get(immutableLocation)).isEqualTo(coordinate);
    }
}
