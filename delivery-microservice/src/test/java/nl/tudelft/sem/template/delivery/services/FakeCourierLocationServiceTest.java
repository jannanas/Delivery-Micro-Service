package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.external.FakeCourierLocationData;
import nl.tudelft.sem.template.delivery.exceptions.EntityNotFoundException;
import nl.tudelft.sem.template.delivery.external.FakeCourierLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeCourierLocationServiceTest {
    private FakeCourierLocationService courierLocationService;

    @BeforeEach
    void setup() {
        courierLocationService = new FakeCourierLocationService(new FakeCourierLocationData());
        courierLocationService.setLocationOfCourier(1L, new Location().address("Address 1"));
    }

    @Test
    void testConstructorAddsCourier() throws EntityNotFoundException {
        Location location = new Location().country("Country");

        FakeCourierLocationData data = new FakeCourierLocationData();
        data.addCourierLocation(2L, location);
        FakeCourierLocationService service = new FakeCourierLocationService(data);

        assertThat(service.getLocationOfCourier(2L)).isEqualTo(location);
    }

    @Test
    void testCourierNotFound() {
        assertThrows(EntityNotFoundException.class, () -> courierLocationService.getLocationOfCourier(2L));
    }

    @Test
    void testRemovingCourierLocation() {
        courierLocationService.setLocationOfCourier(1L, null);
        assertThrows(EntityNotFoundException.class, () -> courierLocationService.getLocationOfCourier(1L));
    }

    @Test
    void testCourierFound() throws EntityNotFoundException {
        assertThat(courierLocationService.getLocationOfCourier(1L).getAddress())
                .isEqualTo("Address 1");
    }
}
