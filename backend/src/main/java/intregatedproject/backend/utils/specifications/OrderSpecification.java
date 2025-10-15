package intregatedproject.backend.utils.specifications;

import intregatedproject.backend.entities.Order;
import org.springframework.data.jpa.domain.Specification;

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


    public static Specification<Order> hasKeyword(String searchContent) {
        return (root, query, criteriaBuilder) -> {
            if (searchContent == null || searchContent.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNote"), "%" + searchContent + "%"),
                    criteriaBuilder.like(root.get("shippingAddress"), "%" + searchContent + "%")
            );
        };
    }
}

