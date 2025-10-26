package intregatedproject.backend.utils.specifications;

import intregatedproject.backend.entities.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> hasUser(Integer userId, Boolean isSeller) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null || userId <= 0) return criteriaBuilder.conjunction();
            if (isSeller) {
                return criteriaBuilder.equal(root.join("seller").get("id"), userId);
            } else {
                return criteriaBuilder.equal(root.join("buyer").get("id"), userId);
            }
        };
    }
    public static Specification<Order> hasOrderStatus(String orderStatus) {
        return (root, query, criteriaBuilder) -> {
            if (orderStatus == null || orderStatus.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("orderStatus"), orderStatus);
        };
    }

    public static Specification<Order> hasFilterUser(List<String> filterUsers, Boolean isSeller) {
        return (root, query, criteriaBuilder) -> {
            if (filterUsers == null || filterUsers.isEmpty()) return criteriaBuilder.conjunction();
            if (isSeller) {
                return root.join("buyer").get("fullName").in(filterUsers);
            } else {
                return root.join("seller").get("fullName").in(filterUsers);
            }
        };
    }

    public static Specification<Order> hasOrderDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return cb.conjunction();
            }

            ZoneId zone = ZoneId.systemDefault();
            if (startDate != null && endDate != null) {
                Instant start = startDate.atStartOfDay(zone).toInstant();
                Instant end = endDate.plusDays(1).atStartOfDay(zone).toInstant();
                return cb.between(root.get("orderDate"), start, end);
            } else if (startDate != null) {
                Instant start = startDate.atStartOfDay(zone).toInstant();
                return cb.greaterThanOrEqualTo(root.get("orderDate"), start);
            } else {
                Instant end = endDate.plusDays(1).atStartOfDay(zone).toInstant();
                return cb.lessThan(root.get("orderDate"), end);
            }
        };
    }

    public static Specification<Order> hasBrands(List<String> filterBrands) {
        return (root, query, criteriaBuilder) -> {
            if (filterBrands == null || filterBrands.isEmpty()) return criteriaBuilder.conjunction();
            return root.join("orderItems").join("saleItem").get("brand").get("name").in(filterBrands);
        };
    }

    public static Specification<Order> hasIsViewed(Boolean isViewed) {
        return (root, query, criteriaBuilder) -> {
            if (isViewed == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("isViewed"),isViewed);
        };
    }

    public static Specification<Order> hasSearchContent(String searchContent) {
        return (root, query, criteriaBuilder) -> {
            if (searchContent == null || searchContent.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNote"), "%" + searchContent + "%"),
                    criteriaBuilder.like(root.get("shippingAddress"), "%" + searchContent + "%"),
                    criteriaBuilder.like(root.join("orderItems").get("description"), "%" + searchContent + "%")

            );
        };
    }
}

