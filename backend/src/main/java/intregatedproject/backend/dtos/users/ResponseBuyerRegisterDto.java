package intregatedproject.backend.dtos.users;

import lombok.Data;


@Data
public class ResponseBuyerRegisterDto {
    private Integer id;
    private String nickname;
    private String email;
    private String fullName;
    private String role;
    private String status;
}
