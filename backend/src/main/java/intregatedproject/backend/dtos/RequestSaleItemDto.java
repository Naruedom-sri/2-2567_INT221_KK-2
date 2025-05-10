package intregatedproject.backend.dtos;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class RequestSaleItemDto {
    private Integer id;
    private String model;
    public void setModel(){
        this.model = model.trim();
    }
    private Integer ramGb;
    private Integer storageGb;
    private Integer price;
    private ResponseBrandDto brand;
    private String description;
    public void setDescription(){
        this.description = description.trim();
    }
    private BigDecimal screenSizeInch;
    private String color;
    public void setColor(){
        this.color = color.trim();
    }
    private Integer quantity;
    public void setQuantity(){
        if(this.quantity == null || this.quantity < 0){
            this.quantity = 1;
        }
    }
}
