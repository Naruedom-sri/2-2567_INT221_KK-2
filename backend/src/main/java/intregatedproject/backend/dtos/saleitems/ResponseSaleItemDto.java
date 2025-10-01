package intregatedproject.backend.dtos.saleitems;


import intregatedproject.backend.dtos.users.SellerDto;
import lombok.Data;

@Data
public class ResponseSaleItemDto {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private Integer price;
    private String color;
    private String brandName;
    private SellerDto seller;
}
