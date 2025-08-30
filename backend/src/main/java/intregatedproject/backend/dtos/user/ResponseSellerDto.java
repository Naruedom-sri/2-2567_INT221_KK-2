package intregatedproject.backend.dtos.user;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResponseSellerDto extends ResponseBuyerDto {
    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
    private String nationalIdNumber;
    private String nationalIdPhotoFront;
    private String nationalIdPhotoBack;
}
