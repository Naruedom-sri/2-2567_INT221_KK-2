package intregatedproject.backend.dtos.saleitems;

import intregatedproject.backend.dtos.brands.ResponseBrandDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseSaleItemDtoEdit {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private Integer price;
    private BigDecimal screenSizeInch;
    private String description;
    private String color;
    private Integer quantity;
    private ResponseBrandDto brand;
}
