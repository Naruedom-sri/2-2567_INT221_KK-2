package intregatedproject.backend.dtos.orders;

import lombok.Data;

import java.util.List;

@Data
public class PageSellerOrder {
    private List<ResponseSellerOrderDto> content;
    private boolean last;
    private boolean first;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private String sort;
    private Integer page;
}
