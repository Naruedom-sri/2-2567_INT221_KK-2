package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
  List<SaleItemImage> findBySaleItemIdOrderByImageViewOrderAsc(Integer saleItemId);
}
