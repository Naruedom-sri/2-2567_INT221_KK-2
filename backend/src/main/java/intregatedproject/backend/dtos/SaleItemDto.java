package intregatedproject.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItemDto {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private int price;
    @JsonIgnore
    private BrandDto brand;
    public String getBrandName() {
        return brand.getName();
    };
    private String color;



}
