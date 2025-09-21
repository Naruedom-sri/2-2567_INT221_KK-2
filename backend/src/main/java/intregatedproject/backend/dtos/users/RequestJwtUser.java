package intregatedproject.backend.dtos.users;

import lombok.Data;

@Data
public class RequestJwtUser {
    private String email;
    private String password;
}
