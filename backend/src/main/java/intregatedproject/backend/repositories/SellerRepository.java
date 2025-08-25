package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
}