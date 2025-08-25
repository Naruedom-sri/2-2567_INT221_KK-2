package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Integer> {
  }