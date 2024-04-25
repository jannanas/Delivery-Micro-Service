package nl.tudelft.sem.template.delivery.database;

import delivery_microservice.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findDeliveryByOrderId(long id);

    List<Delivery> findDeliveriesByCourierIdNull();

    @Query("SELECT d FROM Delivery d WHERE d.delivered = true")
    List<Delivery> findDeliveredDeliveries();
}
