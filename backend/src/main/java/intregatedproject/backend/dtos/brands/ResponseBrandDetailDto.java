package intregatedproject.backend.dtos.brands;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseBrandDetailDto extends ResponseBrandDto{
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
    private Integer noOfSaleItems;
}
