package intregatedproject.backend.dtos.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SellerDto {
    private Integer id;
    private String email;
    @JsonProperty("fullName")
    private String userName;
    @JsonProperty("userType")
    private String role;
    private String nickName;
}
