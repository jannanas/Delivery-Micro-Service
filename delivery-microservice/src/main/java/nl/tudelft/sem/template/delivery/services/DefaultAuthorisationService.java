package nl.tudelft.sem.template.delivery.services;

import org.springframework.stereotype.Service;
import users_microservice.ApiException;
import users_microservice.api.UserApi;
import users_microservice.model.UsersGetUserTypeIdGet200Response.UserTypeEnum;

@Service
public class DefaultAuthorisationService implements AuthorisationService {

    UserApi userApi;

    public DefaultAuthorisationService(UserApi userApi) {
        this.userApi = userApi;
    }

    /**
     * Checks if user is customer.
     *
     * @param userId Long user id
     * @return boolean
     */
    @Override
    public boolean isCustomer(Long userId) {
        return isUserType(userId, UserTypeEnum.CUSTOMER);
    }

    /**
     * Checks if user is vendor.
     *
     * @param userId Long user id
     * @return boolean
     */
    @Override
    public boolean isVendor(Long userId) {
        return isUserType(userId, UserTypeEnum.VENDOR);
    }

    /**
     * Checks if user is courier.
     *
     * @param userId Long user id
     * @return boolean
     */
    @Override
    public boolean isCourier(Long userId) {
        return isUserType(userId, UserTypeEnum.COURIER);
    }

    /**
     * Checks if user is admin.
     *
     * @param userId Long user id
     * @return boolean
     */
    @Override
    public boolean isAdmin(Long userId) {
        return isUserType(userId, UserTypeEnum.ADMIN);
    }

    /**
     * Checks if user is valid. (customer, vendor, courier or admin)
     *
     * @param userId Long user id
     * @return boolean
     */
    @Override
    public boolean isUser(Long userId) {
        return isCustomer(userId) || isVendor(userId) || isCourier(userId) || isAdmin(userId);
    }

    public boolean isValid(Long userId) {
        return userId != null && userId >= 0;
    }

    private boolean isUserType(Long userId, UserTypeEnum userType) {
        if (!isValid(userId)) {
            return false;
        }
        var type = getUserType(userId);
        return (type == userType || type == UserTypeEnum.ADMIN);
    }

    private UserTypeEnum getUserType(Long userId) {
        try {
            return userApi.usersGetUserTypeIdGet(userId).getUserType();
        } catch (ApiException e) {
            return null;
        }
    }

}
