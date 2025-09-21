package intregatedproject.backend.dtos.users;

import lombok.Data;


@Data
public class ResponseBuyerDto {
    private Integer id;
    private String nickname;
//    private String password;
    private String email;
    private String fullname;
    private String role;
    private String status;
}
