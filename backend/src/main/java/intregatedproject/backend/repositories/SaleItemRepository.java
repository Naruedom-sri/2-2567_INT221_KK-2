package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    List<SaleItem> findAllByOrderByBrand_NameAsc();
    List<SaleItem> findAllByOrderByBrand_NameDesc();
    List<SaleItem> findAllByOrderByCreatedOnAsc();
    List<SaleItem> findAllByOrderByCreatedOnDesc();
    List<SaleItem> findByBrand_NameIn(List<String> brands, Sort sort);
}