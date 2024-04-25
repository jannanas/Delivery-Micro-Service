package nl.tudelft.sem.template.delivery.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import users_microservice.ApiException;
import users_microservice.api.UserApi;
import users_microservice.model.UsersGetUserTypeIdGet200Response;
import users_microservice.model.UsersGetUserTypeIdGet200Response.UserTypeEnum;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class DefaultAuthorisationServiceTest {
    @Mock
    private UserApi userApiMock;

    @InjectMocks
    private DefaultAuthorisationService authorisationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Is Customer

    @Test
    void testIsCustomer_Valid() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.CUSTOMER));

        boolean result = authorisationService.isCustomer(1L);
        assertTrue(result);
    }

    @Test
    void testIsCustomer_NotCustomer() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.VENDOR));

        boolean result = authorisationService.isCustomer(1L);
        assertFalse(result);
    }

    @Test
    void testIsCustomer_ApiException() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenThrow(new ApiException());

        boolean result = authorisationService.isCustomer(1L);
        assertFalse(result);
    }

    @Test
    void testIsCustomer_InvalidId() throws ApiException {
        boolean result = authorisationService.isCustomer(-1L);
        assertFalse(result);
    }

    // Is Vendor

    @Test
    void testIsVendor_Valid() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.VENDOR));

        boolean result = authorisationService.isVendor(1L);
        assertTrue(result);
    }

    @Test
    void testIsVendor_NotVendor() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.CUSTOMER));

        boolean result = authorisationService.isVendor(1L);
        assertFalse(result);
    }

    @Test
    void testIsVendor_ApiException() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenThrow(new ApiException());

        boolean result = authorisationService.isVendor(1L);
        assertFalse(result);
    }

    @Test
    void testIsVendor_InvalidId() throws ApiException {
        boolean result = authorisationService.isVendor(-1L);
        assertFalse(result);
    }

    // Is Admin

    @Test
    void testIsAdmin_Valid() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.ADMIN));

        boolean result = authorisationService.isAdmin(1L);
        assertTrue(result);
    }

    @Test
    void testIsAdmin_NotAdmin() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.CUSTOMER));

        boolean result = authorisationService.isAdmin(1L);
        assertFalse(result);
    }

    @Test
    void testIsAdmin_ApiException() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenThrow(new ApiException());

        boolean result = authorisationService.isAdmin(1L);
        assertFalse(result);
    }

    @Test
    void testIsAdmin_InvalidId() throws ApiException {
        boolean result = authorisationService.isAdmin(-1L);
        assertFalse(result);
    }

    // Is Courier

    @Test
    void testIsCourier_Valid() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.COURIER));

        boolean result = authorisationService.isCourier(1L);
        assertTrue(result);
    }

    @Test
    void testIsCourier_NotCourier() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.CUSTOMER));

        boolean result = authorisationService.isCourier(1L);
        assertFalse(result);
    }

    @Test
    void testIsCourier_ApiException() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenThrow(new ApiException());

        boolean result = authorisationService.isCourier(1L);
        assertFalse(result);
    }


    // Is User

    @Test
    void testIsUser_Valid_Courier() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.COURIER));

        boolean result = authorisationService.isUser(1L);
        assertTrue(result);
    }

    @Test
    void testIsUser_Valid_Vendor() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.VENDOR));

        boolean result = authorisationService.isUser(1L);
        assertTrue(result);
    }

    @Test
    void testIsUser_Valid_Customer() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.CUSTOMER));

        boolean result = authorisationService.isUser(1L);
        assertTrue(result);
    }

    @Test
    void testIsUser_Valid_Admin() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(UserTypeEnum.ADMIN));

        boolean result = authorisationService.isUser(1L);
        assertTrue(result);
    }

    @Test
    void testIsUser_NotUser_Exception() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenThrow(new ApiException());

        boolean result = authorisationService.isUser(1L);
        assertFalse(result);
    }

    @Test
    void testIsUser_NotUser_Null() throws ApiException {
        when(userApiMock.usersGetUserTypeIdGet(1L))
            .thenReturn(new UsersGetUserTypeIdGet200Response().userType(null));

        boolean result = authorisationService.isUser(1L);
        assertFalse(result);
    }


    // Is Valid ID

    @Test
    void testIsCourier_InvalidId() throws ApiException {
        boolean result = authorisationService.isCourier(-1L);
        assertFalse(result);
    }

    @Test
    void testIsValid_ValidUserId() {
        Long userId = 0L;
        boolean result = authorisationService.isValid(userId);
        assertTrue(result);
    }

    @Test
    void testIsValid_NullUserId() {
        Long userId = null;
        boolean result = authorisationService.isValid(userId);
        assertFalse(result);
    }

    @Test
    void testIsValid_InvalidUserId() {
        Long userId = -1L;
        boolean result = authorisationService.isValid(userId);
        assertFalse(result);
    }

}

