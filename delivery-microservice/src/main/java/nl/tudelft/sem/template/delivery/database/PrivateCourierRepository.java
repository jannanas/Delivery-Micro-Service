package nl.tudelft.sem.template.delivery.database;

import delivery_microservice.model.PrivateCourier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateCourierRepository extends JpaRepository<PrivateCourier, Long> {
    PrivateCourier findOneByVendorId(Long vendorId);

    List<PrivateCourier> findAllByVendorId(Long vendorId);
}
