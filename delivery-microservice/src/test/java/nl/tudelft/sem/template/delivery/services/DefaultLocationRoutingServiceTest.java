package nl.tudelft.sem.template.delivery.services;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.tudelft.sem.template.delivery.services.LocationRoutingService.BIKE_AVERAGE_SPEED;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultLocationRoutingServiceTest {
    private DefaultLocationRoutingService service;
    private final Coordinate mekelweg5 = new Coordinate(52.002110, 4.373220);
    private final Coordinate gasselterboerveenschemond = new Coordinate(53.00919, 6.88922);
    private final Coordinate fellowship = new Coordinate(51.99039, 4.37759);
    private final Coordinate universityOfWellington = new Coordinate(-41.29011, 174.76788);

    @BeforeEach
    void setup() {
        service = new DefaultLocationRoutingService();
    }

    @Test
    void testCalculateDistanceBetweenCoordinates() {
        assertThat(service.calculateDistanceBetweenCoordinates(mekelweg5, gasselterboerveenschemond))
                .isCloseTo(204000, Offset.offset(500)); // 204km with 500 meter margin
    }

    @Test
    void testCalculateTravelTime() {
        assertThat(service.calculateTravelTime(mekelweg5, fellowship, BIKE_AVERAGE_SPEED))
                .isCloseTo(5, Offset.offset(1)); // 5 minutes with 1 minute margin
    }

    @Test
    void testCalculateDistanceLargeDistance() {
        assertThat(service.calculateDistanceBetweenCoordinates(universityOfWellington, mekelweg5))
                .isCloseTo(18_619_000, Offset.offset(1_000)); // ~19,000 km with margin of 1km
    }
}
