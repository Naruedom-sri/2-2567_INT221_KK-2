package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SaleItemRepository extends JpaRepository<SaleItem, Integer>, JpaSpecificationExecutor<SaleItem> {
//    @Query("SELECT s FROM SaleItem s JOIN s.brand b")
//    Page<SaleItem> findAllWithPage(Pageable pageable);
//
//    Page<SaleItem> findAllByBrand_NameIn(List<String> filterBrands, Pageable pageable);
//
//    Page<SaleItem> findAllByBrand_NameInAndPriceBetween(List<String> filterBrands, Integer minPrice, Integer maxPrice, Pageable pageable);
//
//    Page<SaleItem> findAllByPriceBetween(Integer minPrice, Integer maxPrice,
//                                         Pageable pageable);
//
//    @Query("""
//            SELECT s FROM SaleItem s
//            WHERE s.storageGb IN ?1 OR (?2 = true AND s.storageGb IS NULL)
//            """)
//    Page<SaleItem> findAllByStorageGb(List<Integer> filterStorages, boolean includeNull, Pageable pageable);
//
//    @Query("""
//            SELECT s FROM SaleItem s JOIN s.brand b
//            WHERE b.name IN ?1 AND s.storageGb IN ?2 OR (?3 = true AND s.storageGb IS NULL)
//            """)
//    Page<SaleItem> findAllByBrandNameAndStorageGb(List<String> filterBrands, List<Integer> filterStorages, boolean includeNull, Pageable pageable);
//
//    @Query("""
//            SELECT s FROM SaleItem s JOIN s.brand b
//            WHERE s.storageGb IN ?1 OR (?2 = true AND s.storageGb IS NULL) AND s.price BETWEEN ?3 AND ?4
//            """)
//    Page<SaleItem> findAllByStorageGbAndPrice(List<Integer> filterStorages, boolean includeNull, Integer minPrice, Integer maxPrice, Pageable pageable);
//
//    @Query("""
//            SELECT s FROM SaleItem s JOIN s.brand b
//            WHERE b.name IN ?1 AND s.storageGb IN ?2 AND s.price BETWEEN ?4 AND ?5  OR (?3 = true AND s.storageGb IS NULL)
//            """)
//    Page<SaleItem> findAllByBrandNameAndStorageGbAndPrice(
//            List<String> brandNames,
//            List<Integer> storageSizes,
//            boolean includeNull,
//            Integer minPrice, Integer maxPrice,
//            Pageable pageable
//    );
}