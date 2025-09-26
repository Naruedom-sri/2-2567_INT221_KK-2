package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
}
