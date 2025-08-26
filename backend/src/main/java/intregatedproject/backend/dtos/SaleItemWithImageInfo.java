package intregatedproject.backend.dtos;

import intregatedproject.backend.dtos.saleitems.RequestSaleItemDto;
import lombok.Data;

import java.util.List;

@Data
public class  SaleItemWithImageInfo {
    private RequestSaleItemDto saleItem;
    private List<SaleItemImageRequest> imageInfos;
}
