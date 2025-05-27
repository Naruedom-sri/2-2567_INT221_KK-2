package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    List<SaleItem> findByBrand_NameIn(List<String> brands, Sort sort);

    @Query("SELECT s FROM SaleItem s JOIN s.brand b ORDER BY b.name ASC, s.createdOn ASC, s.id ASC")
    List<SaleItem> findAllSortedByBrandNameAsc();

    @Query("SELECT s FROM SaleItem s JOIN s.brand b ORDER BY b.name DESC, s.createdOn ASC, s.id ASC")
    List<SaleItem> findAllSortedByBrandNameDesc();
}