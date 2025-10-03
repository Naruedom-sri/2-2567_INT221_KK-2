package intregatedproject.backend.dtos.orders;

import intregatedproject.backend.dtos.users.SellerDto;
import lombok.Data;


import java.time.Instant;
import java.util.List;

@Data
public class ResponseOrderDto {
    private Integer id;
    private Integer buyerId;
    private SellerDto seller;
    private Instant orderDate;
    private Instant paymentDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDto>  orderItems;
    private String orderStatus;
}
