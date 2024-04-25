package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.PrivateCourier;
import nl.tudelft.sem.template.delivery.database.PrivateCourierRepository;
import org.springframework.stereotype.Service;
import users_microservice.ApiException;
import users_microservice.api.CourierApi;
import users_microservice.model.Courier;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DefaultCourierService implements  CourierService {
    private final PrivateCourierRepository privateCourierRepository;
    private CourierApi courierApi;

    /**
     * Injects PrivateCourierRepository.
     *
     * @param privateCourierRepository PrivateCourierRepository to be used
     */
    public DefaultCourierService(PrivateCourierRepository privateCourierRepository, CourierApi courierApi) {
        this.privateCourierRepository = privateCourierRepository;
        this.courierApi = courierApi;
    }

    public boolean privateCourierExists(long courierId) {
        return privateCourierRepository.existsById(courierId);
    }

    public Optional<PrivateCourier> getPrivateCourier(final Long courierId) {
        return privateCourierRepository.findById(courierId);
    }

    public Optional<PrivateCourier> getSomePrivateCourierByVendor(final Long vendorId) {
        return Optional.ofNullable(privateCourierRepository.findOneByVendorId(vendorId));
    }

    public PrivateCourier savePrivateCourier(final PrivateCourier privateCourier) {
        return privateCourierRepository.save(privateCourier);
    }

    public List<PrivateCourier> getAllPrivateCouriersByVendor(Long vendorId) {
        return privateCourierRepository.findAllByVendorId(vendorId);
    }

    /**
     * Retrieves a public courier for a delivery. Since there is currently no reliable way to determine
     * the availability or criteria for selecting couriers (e.g., proximity, capacity to take another order),
     * this method randomly selects a courier from the available options.
     *
     * @return An Optional containing the selected courier for the delivery, or empty Optional
     *         if there are no couriers available.
     */
    public Optional<Courier> getSomePublicCourier() {
        try {
            List<Courier> allCouriers = courierApi.couriersGet();

            // Selects random index to choose
            Random random = new Random();
            return Optional.ofNullable(allCouriers.get(random.nextInt(allCouriers.size())));
        } catch (ApiException exception) {
            return Optional.empty();
        }
    }

    public Courier updatePublicCourier(Courier courier) throws ApiException {
        return courierApi.couriersIdPut(courier.getId(), courier);
    }
}
