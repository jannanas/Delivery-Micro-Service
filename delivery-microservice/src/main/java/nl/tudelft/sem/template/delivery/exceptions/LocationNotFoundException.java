package nl.tudelft.sem.template.delivery.exceptions;

import delivery_microservice.model.Location;

public class LocationNotFoundException extends Exception {
    public LocationNotFoundException(Location location) {
        super("Location " + locationToString(location) + " not found");
    }

    private static String locationToString(Location location) {
        return String.format("%s, %s %s, %s",
                location.getAddress(), location.getPostalCode(),
                location.getCity(), location.getCountry());
    }
}
