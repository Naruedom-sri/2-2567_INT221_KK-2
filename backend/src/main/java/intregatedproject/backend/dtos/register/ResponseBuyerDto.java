package intregatedproject.backend.dtos.register;

import lombok.Data;


@Data
public class ResponseBuyerDto {
    private Integer id;
    private String nickname;
    private String password;
    private String email;
    private String fullname;
    private String role;
    private String status = "INACTIVE";
}
