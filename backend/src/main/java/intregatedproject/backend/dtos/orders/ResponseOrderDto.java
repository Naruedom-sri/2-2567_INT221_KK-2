package intregatedproject.backend.dtos.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import intregatedproject.backend.dtos.saleitems.ResponseSaleItemDto;
import intregatedproject.backend.dtos.users.SellerDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResponseOrderDto {
    private Integer id;
    private Integer buyerId;
    @JsonProperty("seller")
    private SellerDto user;
    private Instant orderDate;
    private String shippingAddress;
    private String orderNote;
    @JsonProperty("orderItems")
    private List<ResponseSaleItemDto>  saleItems;
    private String orderStatus;
}
