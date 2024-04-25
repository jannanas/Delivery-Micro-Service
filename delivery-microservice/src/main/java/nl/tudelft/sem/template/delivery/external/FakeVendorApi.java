package nl.tudelft.sem.template.delivery.external;

import users_microservice.api.VendorApi;
import users_microservice.model.Vendor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class FakeVendorApi extends VendorApi {
    @Override
    public List<Vendor> vendorsVerifiedGet() {

        return List.of(
                new Vendor()
                        .id(3L)
                        .name("John")
                        .surname("Doe")
                        .email("johndoe@gmail.com")
                        .openingHour(OffsetDateTime.of(2024, 1, 6, 9, 0, 0, 0, ZoneOffset.UTC))
                        .closingHour(OffsetDateTime.of(2024, 1, 6, 5, 0, 0, 0, ZoneOffset.UTC))
                        .location(new users_microservice.model.Location()
                                .country("Netherlands")
                                .city("Delft")
                                .street("Mekelweg 3")
                                .streetNumber("2628 CD"))
                        .pastOrders(new ArrayList<>())
        );
    }
}
