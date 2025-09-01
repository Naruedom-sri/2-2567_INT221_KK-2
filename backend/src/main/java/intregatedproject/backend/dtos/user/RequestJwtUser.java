package intregatedproject.backend.dtos.user;

import lombok.Data;

@Data
public class RequestJwtUser {
    private String email;
    private String password;
}
