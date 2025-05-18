package intregatedproject.backend.dtos;

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

    public void setName(String name){
        if(name != null && !name.trim().isEmpty()){
            this.name = name.trim();
        }else {
            this.name = null;
        }
    }

    public void setWebsiteUrl(String websiteUrl){
        if(websiteUrl != null && !websiteUrl.trim().isEmpty()){
            this.websiteUrl = websiteUrl.trim();
        }else {
            this.websiteUrl = null;
        }
    }

    public void setIsActive(Boolean isActive){
        this.isActive = Objects.requireNonNullElse(isActive, true);
    }

    public void setCountryOfOrigin(String countryOfOrigin){
        if(countryOfOrigin != null && !countryOfOrigin.trim().isEmpty()){
            this.countryOfOrigin = countryOfOrigin.trim();
        }else {
            this.countryOfOrigin = null;
        }
    }
}
