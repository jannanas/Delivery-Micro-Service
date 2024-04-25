package nl.tudelft.sem.template.delivery.services;

import delivery_microservice.model.PrivateCourier;
import users_microservice.ApiException;
import users_microservice.model.Courier;

import java.util.List;
import java.util.Optional;

public interface CourierService {
    /**
     * Checks whether private courier exists.
     *
     * @param vendorId Long to identify courier
     * @return boolean
     */
    boolean privateCourierExists(long vendorId);

    /**
     * Finds a specific PrivateCourier based on unique id.
     *
     * @param courierId Long courier id
     * @return None or the PrivateCourier
     */
    Optional<PrivateCourier> getPrivateCourier(final Long courierId);

    /**
     * Finds some private courier based on vendor it is tied to.
     *
     * @param vendorId Long id for vendor
     * @return None or some PrivateCourier
     */
    Optional<PrivateCourier> getSomePrivateCourierByVendor(final Long vendorId);

    /**
     * Saves private courier by replacing existing entity or creating new .
     *
     * @param privateCourier PrivateCourier to be saved
     * @return Saved courier
     */
    PrivateCourier savePrivateCourier(final PrivateCourier privateCourier);

    /**
     * Retrieves a public courier for a delivery. Since there is currently no reliable way to determine
     * the availability or criteria for selecting couriers (e.g., proximity, capacity to take another order),
     * this method randomly selects a courier from the available options.
     *
     * @return An Optional containing the selected courier for the delivery, or empty Optional
     *         if there are no couriers available.
     */
    Optional<Courier> getSomePublicCourier();

    /**
     * Save public courier by sending it back to the user service.
     * Used for updating what orders a courier has completed and their rating.
     *
     * @return Courier that was updated
     */
    Courier updatePublicCourier(Courier courier) throws ApiException;

    List<PrivateCourier> getAllPrivateCouriersByVendor(Long vendorId);

}