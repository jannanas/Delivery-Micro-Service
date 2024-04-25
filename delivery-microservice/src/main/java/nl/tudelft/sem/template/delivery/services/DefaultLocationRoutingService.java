package nl.tudelft.sem.template.delivery.services;

import org.springframework.stereotype.Service;

/**
 * This service assumes the earth is completely flat and people can travel as the crow flies.
 */
@Service
public class DefaultLocationRoutingService implements LocationRoutingService {
    @Override
    public int calculateDistanceBetweenCoordinates(Coordinate coordinate1, Coordinate coordinate2) {
        // haversine formula (https://en.wikipedia.org/wiki/Haversine_formula)
        int meanEarthRadiusMeters = 6_371_009;

        double longitudeDifference = Math.toRadians(coordinate2.getLongitude() - coordinate1.getLongitude());
        double latitudeDifference = Math.toRadians(coordinate2.getLatitude() - coordinate1.getLatitude());

        double latitude1 = Math.toRadians(coordinate1.getLatitude());
        double latitude2 = Math.toRadians(coordinate2.getLatitude());

        double a = Math.pow(Math.sin(latitudeDifference / 2), 2)
                + Math.pow(Math.sin(longitudeDifference / 2), 2) * Math.cos(latitude1) * Math.cos(latitude2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (meanEarthRadiusMeters * c);
    }

    @Override
    public int calculateTravelTime(Coordinate coordinate1, Coordinate coordinate2, int averageSpeedKph) {
        int distanceInMeters = calculateDistanceBetweenCoordinates(coordinate1, coordinate2);
        double speedInMetersPerMinute = averageSpeedKph * 1000.0 / 60.0;

        return (int) Math.ceil(distanceInMeters / speedInMetersPerMinute);
    }
}
