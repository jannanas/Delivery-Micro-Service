package nl.tudelft.sem.template.delivery.config;

import lombok.Getter;
import nl.tudelft.sem.template.delivery.external.FakeOrderApi;
import nl.tudelft.sem.template.delivery.external.FakeUserApi;
import nl.tudelft.sem.template.delivery.external.FakeVendorApi;
import orders_microservice.api.OrderApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import users_microservice.api.CourierApi;
import users_microservice.api.UserApi;
import users_microservice.api.VendorApi;

@Configuration
public class AppConfig {

    @Getter
    private final Environment environment;

    public AppConfig(Environment environment) {
        this.environment = environment;
    }


    /**
     * Create a bean for the user api. If the environment is dev, use a fake implementation.
     *
     * @return The data source.
     */
    @Bean
    public UserApi userApi() {
        var env = environment.getProperty("env");

        if ("dev".equals(env)) {
            return new FakeUserApi();
        }

        return new UserApi();
    }

    /**
     * Create a bean for the order api. If the environment is dev, use a fake implementation.
     *
     * @return The data source.
     */
    @Bean
    public OrderApi orderApi() {
        var env = environment.getProperty("env");

        if ("dev".equals(env)) {
            return new FakeOrderApi();
        }

        return new OrderApi();
    }

    /**
     * Create a bean for the vendor api. If the environment is dev, use a fake implementation.
     *
     * @return The data source.
     */
    @Bean
    public VendorApi vendorApi() {
        var env = environment.getProperty("env");

        if ("dev".equals(env)) {
            return new FakeVendorApi();
        }

        return new VendorApi();
    }

    /**
     * Create a bean for the vendor api. If the environment is dev, use a fake implementation.
     *
     * @return The data source.
     */
    @Bean
    public CourierApi courierApi() {
        return new CourierApi();
    }
}
