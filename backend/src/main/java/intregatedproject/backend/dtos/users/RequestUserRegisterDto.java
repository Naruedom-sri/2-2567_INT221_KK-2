package intregatedproject.backend.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestUserRegisterDto {
    private Integer id;
    private String nickname;
    @NotBlank(message = "Password is required", groups = BuyerChecks.class)
    private String password;
    @Email(message = "Invalid email format", groups = BuyerChecks.class)
    @NotBlank(message = "Email is required", groups = BuyerChecks.class)
    private String email;
    private String fullName;

    @NotBlank(message = "Role is required", groups = BuyerChecks.class)
    @Pattern(regexp = "^(buyer|seller)$", message = "Role must be 'buyer' or 'seller'", groups = BuyerChecks.class)
    private String role;

    private String status = "INACTIVE";;


    @NotBlank(message = "Mobile number is required", groups = SellerChecks.class)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits", groups = SellerChecks.class)
    private String mobileNumber;

    @NotBlank(message = "Bank account number is required", groups = SellerChecks.class)
    private String bankAccountNumber;

    @NotBlank(message = "Bank name is required", groups = SellerChecks.class)
    private String bankName;

    @NotBlank(message = "National ID number is required", groups = SellerChecks.class)
    @Pattern(regexp = "^[0-9]{13}$", message = "National ID number must be 13 digits", groups = SellerChecks.class)
    private String nationalIdNumber;
    private String nationalIdPhotoFront;
    private String nationalIdPhotoBack;

//    public void setPassword(String password) {
//        this.password = PasswordUtils.hashPassword(password);
//    }

    public void setStatus(String status) {
        this.status = status == null || status.isBlank() ? "INACTIVE" : status;
    }

    public void setRole(String role) {
        if (role != null) {
            this.role = role.toLowerCase().trim();
        } else {
            this.role = null;
        }
    }
}

