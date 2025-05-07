package intregatedproject.backend.dtos;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class RequestSaleItemDto {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private int price;
    private BrandDto brand;
    private String description;
    private BigDecimal screenSizeInch;
    private String color;
    private Integer quantity;
}
