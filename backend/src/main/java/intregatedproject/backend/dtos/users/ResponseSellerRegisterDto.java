package intregatedproject.backend.dtos.users;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResponseSellerRegisterDto extends ResponseBuyerRegisterDto {
    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
    private String nationalIdNumber;
    private String nationalIdPhotoFront;
    private String nationalIdPhotoBack;
}
