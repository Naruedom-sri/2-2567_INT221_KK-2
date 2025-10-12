package intregatedproject.backend.dtos.orders;

import intregatedproject.backend.dtos.users.BuyerDto;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseSellerDto {
    private Integer id;
    private Integer sellerId;
    private BuyerDto buyer;
    private Instant orderDate;
    private Instant paymentDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDto> orderItems = new ArrayList<>();
    private String orderStatus;
}
