package intregatedproject.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItemDto {
    private BrandDto brand;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private int price;
}
