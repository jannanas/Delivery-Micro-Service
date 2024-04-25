package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImmutableLocationTest {
    private ImmutableLocation location;

    @BeforeEach
    void setup() {
        location = new ImmutableLocation("Country", "City", "Postal code", "Address");
    }

    @Test
    void getCountry_test() {
        assertThat(location.getCountry()).isEqualTo("Country");
    }

    @Test
    void getCity_test() {
        assertThat(location.getCity()).isEqualTo("City");
    }

    @Test
    void getPostalCode_test() {
        assertThat(location.getPostalCode()).isEqualTo("Postal code");
    }

    @Test
    void getAddress_test() {
        assertThat(location.getAddress()).isEqualTo("Address");
    }

    @Test
    void createFromLocation_test() {
        Location location = new Location().country("Country").city("City").postalCode("Postal code").address("Address");
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        assertThat(immutableLocation.getCountry()).isEqualTo("Country");
        assertThat(immutableLocation.getCity()).isEqualTo("City");
        assertThat(immutableLocation.getPostalCode()).isEqualTo("Postal code");
        assertThat(immutableLocation.getAddress()).isEqualTo("Address");
    }

    @Test
    void createMutableLocation_test() {
        Location location = new Location().country("Country").city("City").postalCode("Postal code").address("Address");
        ImmutableLocation immutableLocation = ImmutableLocation.createFromLocation(location);
        assertThat(immutableLocation.createMutableLocation()).isEqualTo(location);
    }
}
