package intregatedproject.backend.dtos.saleitems;


import intregatedproject.backend.dtos.SaleItemImageResponse;
import intregatedproject.backend.dtos.brands.ResponseBrandDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class RequestSaleItemDto {
    private Integer id;
    private String model;
    private Integer ramGb;
    private Integer storageGb;
    private Integer price;
//    private ResponseBrandDto brand;
    private Integer brandId;
    private String description;
    private BigDecimal screenSizeInch;
    private String color;
    private Integer quantity;


    public void setId(Integer id) {
        this.id = null;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null || quantity < 0 ? 1 : quantity;
    }

    public void setModel(String model) {
        this.model = model != null && !model.isBlank() ? model.trim() : null;
    }

    public void setDescription(String description) {
        this.description = description != null && !description.isBlank() ? description.trim() : null;
    }

    public void setColor(String color) {
        this.color = color != null && !color.isBlank() ? color.trim() : null;
    }

}
