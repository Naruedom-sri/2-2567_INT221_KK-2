package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> , JpaSpecificationExecutor<SaleItem> {
  }