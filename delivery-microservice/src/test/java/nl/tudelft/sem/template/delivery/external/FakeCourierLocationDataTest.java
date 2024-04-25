package nl.tudelft.sem.template.delivery.external;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeCourierLocationDataTest {
    @Test
    void addCourierLocation_test() {
        Location location = new Location().country("Netherlands");
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);

        FakeCourierLocationData data = new FakeCourierLocationData();
        data.addCourierLocation(1L, location);

        assertThat(data.size()).isEqualTo(1);
        assertThat(data.get(1L)).isEqualTo(immutableLocation);
    }
}
