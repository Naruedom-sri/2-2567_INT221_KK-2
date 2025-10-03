package intregatedproject.backend.utils;

import intregatedproject.backend.entities.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> hasOrderId(Integer orderId) {
        return (root, query, cb) -> {
            if (orderId == null) return cb.conjunction();
            return cb.equal(root.get("id"), orderId);
        };
    }

    public static Specification<Order> hasKeyword(String searchContent) {
        return (root, query, cb) -> {
            if (searchContent == null || searchContent.isBlank()) return cb.conjunction();
            // ตัวอย่าง: ค้นหาใน orderNote หรือ shippingAddress
            return cb.or(
                    cb.like(root.get("orderNote"), "%" + searchContent + "%"),
                    cb.like(root.get("shippingAddress"), "%" + searchContent + "%")
            );
        };
    }
}

