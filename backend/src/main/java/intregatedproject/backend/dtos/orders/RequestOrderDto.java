package intregatedproject.backend.dtos.orders;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class RequestOrderDto {
    private Integer buyerId;
    private Integer sellerId;
    private Instant orderDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDto> orderItems;
    private String orderStatus;
    private Boolean isViewed = false;

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote.isBlank() ? null : orderNote;
    }

}
