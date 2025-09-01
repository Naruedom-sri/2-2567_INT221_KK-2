package intregatedproject.backend.dtos.authentications;

import lombok.Data;

@Data
public class ResponseLogin {
    private String access_token;
    private String refresh_token;
}
