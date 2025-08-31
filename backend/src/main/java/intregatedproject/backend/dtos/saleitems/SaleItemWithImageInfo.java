package intregatedproject.backend.dtos.saleitems;

import lombok.Data;

import java.util.List;

@Data
public class  SaleItemWithImageInfo {
    private RequestSaleItemDto saleItem;
    private List<SaleItemImageRequest> imageInfos;
}
