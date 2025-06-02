package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    Page<SaleItem> findByBrand_NameIn(List<String> brandNames, Pageable pageable);

    @Query("SELECT s FROM SaleItem s JOIN s.brand b")
    Page<SaleItem> findAllWithPage(Pageable pageable);
}