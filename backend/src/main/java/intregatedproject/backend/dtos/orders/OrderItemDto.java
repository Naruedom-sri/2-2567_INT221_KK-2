package intregatedproject.backend.dtos.orders;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Integer no;
    private Long saleItemId;
    private BigDecimal price;
    private Integer quantity;
    private String description;
}
