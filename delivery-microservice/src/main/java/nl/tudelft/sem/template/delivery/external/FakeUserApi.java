package nl.tudelft.sem.template.delivery.external;

import users_microservice.api.UserApi;
import users_microservice.model.UsersGetUserTypeIdGet200Response;

public class FakeUserApi extends UserApi {
    /**
     * Fake implementation. Always returns ADMIN.
     *
     * @param id Long user id
     * @return UsersGetUserTypeIdGet200Response with UserTypeEnum.ADMIN
     */
    @Override
    public UsersGetUserTypeIdGet200Response usersGetUserTypeIdGet(Long id) {
        return new UsersGetUserTypeIdGet200Response().userType(UsersGetUserTypeIdGet200Response.UserTypeEnum.ADMIN);
    }
}
