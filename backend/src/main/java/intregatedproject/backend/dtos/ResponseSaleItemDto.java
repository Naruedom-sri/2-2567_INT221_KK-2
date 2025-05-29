package intregatedproject.backend.dtos;

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
}
