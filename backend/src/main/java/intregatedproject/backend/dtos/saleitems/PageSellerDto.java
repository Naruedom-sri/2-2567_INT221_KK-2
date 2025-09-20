package intregatedproject.backend.dtos.saleitems;

import lombok.Data;

import java.util.List;

@Data
public class PageSellerDto {
    private List<ResponseSaleItemDto> content;
    private boolean last;
    private boolean first;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private String sort;
    private Integer page;

}
