package intregatedproject.backend.dtos.users;

import lombok.Data;

@Data
public class RequestUserEditDto {
    private String nickname;
    private String fullName;
}
