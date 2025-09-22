package intregatedproject.backend.dtos.saleitems;

import com.fasterxml.jackson.annotation.JsonProperty;
import intregatedproject.backend.dtos.users.SellerDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSaleItemDto {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private Integer price;
    private String color;
    private String brandName;

    @JsonProperty("seller")
    private SellerDto user;
}
