package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    long countByBrandId(int brandId);
}