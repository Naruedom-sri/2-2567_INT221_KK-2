package intregatedproject.backend.dtos.register;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class RequestRegisterDto {
    private Integer id;
    private String nickname;
    private String password;
    private String email;
    private String fullname;
    private String role;
    private String status = "INACTIVE";

    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
    private String nationalIdNumber;
    private String nationalIdPhotoFront;
    private String nationalIdPhotoBack;
// รับแยกแล้วตรงcontroller
}

