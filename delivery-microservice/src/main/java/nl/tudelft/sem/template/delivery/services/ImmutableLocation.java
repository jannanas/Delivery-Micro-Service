package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import lombok.EqualsAndHashCode;

/**
 * ImmutableLocation class to be used as a key in HashMaps.
 * Normal, generated locations are mutable and this can cause problems when used as keys.
 */
@EqualsAndHashCode
public class ImmutableLocation {
    private final Location location;

    /**
     * Creates a ImmutableLocation using the provided location attributes.
     *
     * @param country The country
     * @param city The city
     * @param postalCode The postalCode
     * @param address The address
     */
    public ImmutableLocation(String country, String city, String postalCode, String address) {
        location = new Location()
            .country(country)
            .city(city)
            .postalCode(postalCode)
            .address(address);
    }

    public String getCountry() {
        return location.getCountry();
    }

    public String getCity() {
        return location.getCity();
    }

    public String getPostalCode() {
        return location.getPostalCode();
    }

    public String getAddress() {
        return location.getAddress();
    }

    /**
     * Create a mutable location from the immutable location with the same properties.
     *
     * @return The mutable object
     */
    public Location createMutableLocation() {
        return new Location()
                .country(getCountry())
                .city(getCity())
                .postalCode(getPostalCode())
                .address(getAddress());
    }

    /**
     * Creates a new ImmutableLocation based on an existing Location.
     *
     * @param location The location
     * @return The ImmutableLocation
     */
    public static ImmutableLocation createFromLocation(Location location) {
        return new ImmutableLocation(location.getCountry(), location.getCity(),
                location.getPostalCode(), location.getAddress());
    }
}
