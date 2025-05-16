package intregatedproject.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBrandDto {
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
}
