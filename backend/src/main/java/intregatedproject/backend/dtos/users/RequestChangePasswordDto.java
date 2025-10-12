package intregatedproject.backend.dtos.users;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestChangePasswordDto {
    @NotNull
    @NotEmpty(message = "Password is required.")
    @Size(max = 14, message = "Password must not exceed 14 characters.")
    private String  oldPassword;
    @NotNull
    @NotEmpty(message = "Password is required.")
    @Size(max = 14, message = "Password must not exceed 14 characters.")
    private String newPassword;
}
