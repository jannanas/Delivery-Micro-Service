package nl.tudelft.sem.template.delivery.exceptions;

import delivery_microservice.model.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationNotFoundExceptionTest {
    @Test
    void testExceptionMessage() {
        Location location = new Location()
                .country("Netherlands")
                .city("Delft")
                .address("Mekelweg 4")
                .postalCode("1234 AB");

        try {
            throw new LocationNotFoundException(location);
        } catch (LocationNotFoundException e) {
            assertThat(e.getMessage())
                    .isEqualTo("Location Mekelweg 4, 1234 AB Delft, Netherlands not found");
        }
    }
}
