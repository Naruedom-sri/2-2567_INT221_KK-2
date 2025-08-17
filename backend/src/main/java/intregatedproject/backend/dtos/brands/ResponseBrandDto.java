package intregatedproject.backend.dtos.brands;

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
}
