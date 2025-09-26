package intregatedproject.backend.dtos.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import intregatedproject.backend.dtos.saleitems.ResponseSaleItemDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class RequestOrderDto {
    private Integer buyerId;
    private Integer sellerId;
    private Instant orderDate;
    private String shippingAddress;
    private String orderNote;
    @JsonProperty("orderItems")
    private List<ResponseSaleItemDto> saleItems;
    private String orderStatus;

}
