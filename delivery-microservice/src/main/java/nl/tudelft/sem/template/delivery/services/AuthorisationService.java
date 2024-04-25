package nl.tudelft.sem.template.delivery.services;

import org.springframework.stereotype.Service;

@Service
public interface AuthorisationService {
    /**
     * Checks if user is customer.
     *
     * @param userId Long user id
     * @return boolean
     */
    boolean isCustomer(Long userId);

    /**
     * Checks if user is vendor.
     *
     * @param userId Long user id
     * @return boolean
     */
    boolean isVendor(Long userId);

    /**
     * Checks if user is courier.
     *
     * @param userId Long user id
     * @return boolean
     */
    boolean isCourier(Long userId);

    /**
     * Checks if user is admin.
     *
     * @param userId Long user id
     * @return boolean
     */
    boolean isAdmin(Long userId);

    /**
     * Checks if user is valid. (customer, vendor, courier or admin)
     *
     * @param userId Long user id
     * @return boolean
     */
    boolean isUser(Long userId);

    /**
     * Checks if an id is valid.
     *
     * @param userId Long id
     * @return boolean
     */
    boolean isValid(Long userId);

}
