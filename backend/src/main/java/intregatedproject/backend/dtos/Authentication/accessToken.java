package intregatedproject.backend.dtos.Authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class accessToken {
    private final String token;
    @JsonIgnore
    public accessToken(String token) {
        this.token = token;
    }

    public String getAccess_token() {
        return token;
    }
}
