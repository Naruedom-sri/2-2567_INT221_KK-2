package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
