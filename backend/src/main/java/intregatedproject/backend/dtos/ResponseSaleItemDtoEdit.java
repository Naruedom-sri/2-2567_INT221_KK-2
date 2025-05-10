package intregatedproject.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSaleItemDtoEdit {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private Integer price;
    private String description;
    private String color;
    private Integer quantity;
    private ResponseBrandDto brand;
}
