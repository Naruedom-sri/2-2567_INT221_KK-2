package intregatedproject.backend.dtos.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import intregatedproject.backend.utils.PasswordUtils;
import lombok.Data;

@Data
public class RequestRegisterDto {
    private Integer id;
    private String nickname;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private String status = "INACTIVE";

    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
    private String nationalIdNumber;
    private String nationalIdPhotoFront;
    private String nationalIdPhotoBack;

    public void setPassword(String password) {
        this.password = PasswordUtils.hashPassword(password);
    }
}

