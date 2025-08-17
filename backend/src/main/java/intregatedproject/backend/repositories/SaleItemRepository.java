package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    @Query("SELECT s FROM SaleItem s JOIN s.brand b")
    Page<SaleItem> findAllWithPage(Pageable pageable);

    Page<SaleItem> findAllByBrand_NameInAndStorageGbInAndPriceBetween(
            List<String> brandNames,
            List<Integer> storageSizes,
            Integer minPrice, Integer maxPrice,
            Pageable pageable
    );
    Page<SaleItem> findAllByBrand_NameIn(List<String> filterBrands, Pageable pageable);
    Page<SaleItem> findAllByStorageGb_In(List<Integer> filterStorages, Pageable pageable);
    Page<SaleItem> findAllByBrand_NameInAndStorageGb_In(List<String> filterBrands,List<Integer> filterStorages, Pageable pageable);
    Page<SaleItem> findAllByPriceBetween(Integer minPrice, Integer maxPrice,
                                         Pageable pageable);

    Page<SaleItem> findAllByBrand_NameInAndPriceBetween(List<String> filterBrands,Integer filterPriceLower, Integer filterPriceUpper, Pageable pageable);

    Page<SaleItem> findAllByStorageGb_InAndPriceBetween(List<Integer> filterStorages, Integer filterPriceLower, Integer filterPriceUpper, Pageable pageable);
}