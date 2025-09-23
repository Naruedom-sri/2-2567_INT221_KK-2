package intregatedproject.backend.dtos.users;

import lombok.Data;

@Data
public class RequestEditUserDto {
    private String nickname;
    private String fullName;
}
