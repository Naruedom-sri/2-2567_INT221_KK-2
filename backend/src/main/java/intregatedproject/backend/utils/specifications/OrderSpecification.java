package intregatedproject.backend.utils.specifications;

import intregatedproject.backend.entities.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> hasUser(Integer userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null || userId <= 0) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.join("buyer").get("id"), userId);
        };
    }

    public static Specification<Order> hasKeyword(String searchContent) {
        return (root, query, criteriaBuilder) -> {
            if (searchContent == null || searchContent.isBlank()) return criteriaBuilder.conjunction();
            // ตัวอย่าง: ค้นหาใน orderNote หรือ shippingAddress
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNote"), "%" + searchContent + "%"),
                    criteriaBuilder.like(root.get("shippingAddress"), "%" + searchContent + "%")
            );
        };
    }
}

