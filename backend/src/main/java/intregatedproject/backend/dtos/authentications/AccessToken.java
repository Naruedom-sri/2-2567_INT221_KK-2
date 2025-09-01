package intregatedproject.backend.dtos.authentications;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AccessToken {
    private final String token;
    @JsonIgnore
    public AccessToken(String token) {
        this.token = token;
    }

    public String getAccess_token() {
        return token;
    }
}
