package intregatedproject.backend.dtos.saleitems;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class ResponseSaleItemDetailDto extends ResponseSaleItemDto {
    private String description;
    private BigDecimal screenSizeInch;
    private Integer quantity;
    private Instant createdOn;
    private Instant updatedOn;
}
