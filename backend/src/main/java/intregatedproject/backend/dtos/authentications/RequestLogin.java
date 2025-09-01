package intregatedproject.backend.dtos.authentications;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RequestLogin {

    @NotNull
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotNull
    @NotEmpty(message = "Password is required")
    @Size(max = 14, message = "Password must not exceed 14 characters")
    private String password;


}
