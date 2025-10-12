package intregatedproject.backend.dtos.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BuyerDto {
    private Integer id;
    private String userName;
}
