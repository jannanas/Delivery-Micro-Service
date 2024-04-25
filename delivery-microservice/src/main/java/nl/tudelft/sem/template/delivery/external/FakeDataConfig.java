package nl.tudelft.sem.template.delivery.external;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.delivery.external.FakeCourierLocationData;
import nl.tudelft.sem.template.delivery.external.FakeGeocodingData;
import nl.tudelft.sem.template.delivery.services.Coordinate;
import nl.tudelft.sem.template.delivery.services.ImmutableLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FakeDataConfig {
    private final List<LocationWithCoordinate> locations = new ArrayList<>();

    /**
     * Adds some dummy mock data to test the application with.
     */
    public FakeDataConfig() {
        locations.add(createLocation(51.99882, 4.37354, "Delft", "2628 CD", "Mekelweg 4"));
        locations.add(createLocation(51.99956, 4.37770, "Delft", "2628 XE", "Van Mourik Broekmanweg 5"));
        locations.add(createLocation(52.01045, 4.36077, "Delft", "2611 CP", "Brabantse Turfmarkt 78"));
        locations.add(createLocation(52.01376, 4.36364, "Delft", "2611 KP", "Trompetstraat 88"));
        locations.add(createLocation(52.07219, 4.30700, "Den Haag", "2512 XW", "Glasblazerslaan 83a"));
        locations.add(createLocation(52.07290, 4.28217, "Den Haag", "2562 TL", "Vinkensteynstraat 1"));
    }

    /**
     * Creates the bean for the data which the FakeGeocodingService uses.
     *
     * @return The generated data
     */
    @Bean
    public FakeGeocodingData fakeGeocodingData() {
        FakeGeocodingData fakeData = new FakeGeocodingData();
        for (LocationWithCoordinate locationWithCoordinate : locations) {
            fakeData.put(locationWithCoordinate.getLocation(), locationWithCoordinate.getCoordinate());
        }

        return fakeData;
    }

    /**
     * Creates the bean for the data which the FakeCourierLocationService uses.
     *
     * @return The generated data
     */
    @Bean
    public FakeCourierLocationData fakeCourierLocationData() {
        FakeCourierLocationData fakeData = new FakeCourierLocationData();
        for (int i = 0; i < locations.size(); i++) {
            fakeData.put((long) i, locations.get(i).getLocation());
        }

        return fakeData;
    }

    private LocationWithCoordinate createLocation(double latitude, double longitude,
                      String city, String postalCode, String address) {

        ImmutableLocation location = new ImmutableLocation("Netherlands", city, postalCode, address);
        Coordinate coordinate = new Coordinate(latitude, longitude);

        return new LocationWithCoordinate(location, coordinate);
    }

    @RequiredArgsConstructor
    @Getter
    public static class LocationWithCoordinate {
        private final ImmutableLocation location;
        private final Coordinate coordinate;
    }
}
