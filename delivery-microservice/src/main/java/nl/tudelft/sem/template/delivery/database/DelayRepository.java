package nl.tudelft.sem.template.delivery.database;

import delivery_microservice.model.Delay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DelayRepository extends JpaRepository<Delay, Long> {
}
