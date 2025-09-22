package intregatedproject.backend.utils;

import intregatedproject.backend.entities.SaleItem;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SaleItemSpecification {
    public static Specification<SaleItem> hasFilterBrands(List<String> filterBrands) {
        return (root, query, criteriaBuilder) -> {
            if (filterBrands == null || filterBrands.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.join("brand").get("name").in(filterBrands);
        };
    }
    public static Specification<SaleItem> hasFilterStorages(List<Integer> filterStorages) {
        return (root, query, criteriaBuilder) ->{
            if (filterStorages == null || filterStorages.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            if (filterStorages.contains(null)) {
                return  criteriaBuilder.or(root.get("storageGb").in(filterStorages), root.get("storageGb").isNull());
            } else {
                return root.get("storageGb").in(filterStorages);
            }
        };
    }
    public static Specification<SaleItem> hasSeller(Integer userId) {
        return (root, query, criteriaBuilder) -> {
            if ( userId == null ||  userId == 0) {
                return criteriaBuilder.conjunction();
            }
            return root.join("user").get("id").in(userId);
        };
    }
    public static Specification<SaleItem> hasDescription(String searchContent) {
        return (root, query, criteriaBuilder) -> {
          if (searchContent == null || searchContent.isBlank()) {
              return criteriaBuilder.conjunction();
          }
          return criteriaBuilder.like(root.get("description"), "%" + searchContent + "%");
        };

    }
    public static Specification<SaleItem> hasColor(String searchContent) {
        return (root, query, criteriaBuilder) -> {
            if (searchContent == null || searchContent.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("color"), "%" + searchContent + "%");
        };

    }

    public static Specification<SaleItem> hasModel(String searchContent) {
        return (root, query, criteriaBuilder) -> {
            if (searchContent == null || searchContent.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("model"), "%" + searchContent + "%");
        };
    }

    public static Specification<SaleItem> hasPrices(Integer filterPriceLower, Integer filterPriceUpper) {
        return (root, query, criteriaBuilder) ->  {
            if (filterPriceLower == null && filterPriceUpper == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get("price"), filterPriceLower, filterPriceUpper);
        };
    }

}
