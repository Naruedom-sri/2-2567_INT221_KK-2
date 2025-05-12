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
    private Integer price;
    private ResponseBrandDto brand;
    private String description;
    private BigDecimal screenSizeInch;
    private String color;
    private Integer quantity;

    public void setId(Integer id) {
        this.id = null;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            this.quantity = 1;
        } else {
            this.quantity = quantity;
        }
    }

    public void setModel(String model) {
        if (model != null && !model.trim().isEmpty()) {
            this.model = model.trim().length() > 60 ? model.trim().substring(0, 60) : model.trim();
        } else {
            this.model = null;
        }

    }

    public void setDescription(String description) {
        this.description = description != null && !description.trim().isEmpty() ? description.trim() : null;
    }

    public void setColor(String color) {
        this.color = color != null && !color.trim().isEmpty() ? color.trim() : null;
    }

}
