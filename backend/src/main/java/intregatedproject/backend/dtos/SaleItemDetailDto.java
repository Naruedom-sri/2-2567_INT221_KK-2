package intregatedproject.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaleItemDetailDto extends SaleItemDto {
    private String description;
    private BigDecimal screenSizeInch;
    private String color;
    private Integer quantity;
}
