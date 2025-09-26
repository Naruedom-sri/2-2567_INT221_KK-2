package intregatedproject.backend.dtos.users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSellerDto extends ResponseBuyerDto{
    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
}
