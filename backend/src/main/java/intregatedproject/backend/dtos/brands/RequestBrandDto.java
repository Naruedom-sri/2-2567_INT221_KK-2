package intregatedproject.backend.dtos.brands;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class RequestBrandDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;

    public void setName(String name) {
        this.name = name != null && !name.isBlank() ? name.trim() : null;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl != null && !websiteUrl.isBlank() ? websiteUrl.trim() : null;

    }

    public void setIsActive(Boolean isActive) {
        this.isActive = Objects.requireNonNullElse(isActive, true);
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin != null && !countryOfOrigin.isBlank() ? countryOfOrigin.trim() : null;
    }
}
