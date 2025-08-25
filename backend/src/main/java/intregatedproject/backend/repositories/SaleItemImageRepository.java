package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
  }