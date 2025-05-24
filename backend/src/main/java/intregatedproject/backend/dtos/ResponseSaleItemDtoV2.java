package intregatedproject.backend.dtos;

import intregatedproject.backend.entities.SaleItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseSaleItemDtoV2 {
    private List<SaleItem> saleItems;
    private boolean last;
    private boolean first;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private String sort;
    private Integer page;
}
