package intregatedproject.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBrandDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
    private Integer noOfSaleItems;

    public void setName(String name){
        if(name != null && !name.isEmpty()){
            this.name = name.trim();
        }else {
            this.name = null;
        }
    }
}
