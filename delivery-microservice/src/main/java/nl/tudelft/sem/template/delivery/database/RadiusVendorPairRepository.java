package nl.tudelft.sem.template.delivery.database;

import delivery_microservice.model.RadiusVendorPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RadiusVendorPairRepository extends JpaRepository<RadiusVendorPair, Long> {
}
