package intregatedproject.backend.dtos.saleitems;

import intregatedproject.backend.dtos.SaleItemImageResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseSaleItemImageDtoV2 extends ResponseSaleItemDetailDto {
    private List<SaleItemImageResponse> saleItemImages = new ArrayList<>();
}
