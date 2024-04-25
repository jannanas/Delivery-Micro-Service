package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.external.FakeGeocodingData;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;
import nl.tudelft.sem.template.delivery.external.FakeGeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeGeocodingServiceTest {
    private FakeGeocodingService geocodingService;

    @BeforeEach
    void setup() {
        geocodingService = new FakeGeocodingService(new FakeGeocodingData());
    }

    @Test
    void testConstructorAddsLocation() throws LocationNotFoundException {
        Location location = new Location()
                .country("NL")
                .city("Delft")
                .postalCode("1234 AB")
                .address("Mekelweg 5");
        Coordinate coordinate = new Coordinate(1, 2);

        FakeGeocodingData data = new FakeGeocodingData();
        data.addLocation(location, coordinate);
        FakeGeocodingService service = new FakeGeocodingService(data);

        assertThat(service.getCoordinatesFromLocation(location)).isEqualTo(coordinate);
    }

    @Test
    void testAddressNotFound() {
        Location location = new Location().address("Mekelweg 4");
        assertThrows(LocationNotFoundException.class, () -> geocodingService.getCoordinatesFromLocation(location));
    }

    @Test
    void testRemovingLocation() {
        Location location = new Location().address("Mekelweg 4");
        geocodingService.setLocationCoordinates(location, new Coordinate(1, 2));
        geocodingService.setLocationCoordinates(location, null);
        assertThrows(LocationNotFoundException.class, () -> geocodingService.getCoordinatesFromLocation(location));
    }

    @Test
    void testAddressFound() throws LocationNotFoundException {
        Location location = new Location().address("Mekelweg 5");
        Coordinate coordinate = new Coordinate(52.002110, 4.373220);

        geocodingService.setLocationCoordinates(location, coordinate);

        assertThat(geocodingService.getCoordinatesFromLocation(location)).isEqualTo(coordinate);
    }
}
