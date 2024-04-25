package nl.tudelft.sem.template.delivery.services;

public interface LocationRoutingService {
    int BIKE_AVERAGE_SPEED = 15;

    /**
     * Calculates the distance between two coordinates in meters.
     *
     * @param coordinate1 The first coordinate
     * @param coordinate2 The second coordinate
     * @return the distance in meters
     */
    int calculateDistanceBetweenCoordinates(Coordinate coordinate1, Coordinate coordinate2);

    /**
     * Calculates the time it would take to travel between two coordinates given an average speed in minutes.
     *
     * @param coordinate1 The first coordinate
     * @param coordinate2 The second coordinate
     * @param averageSpeedKph The average speed in kilometers per hour
     * @return The time in minutes
     */
    int calculateTravelTime(Coordinate coordinate1, Coordinate coordinate2, int averageSpeedKph);
}
