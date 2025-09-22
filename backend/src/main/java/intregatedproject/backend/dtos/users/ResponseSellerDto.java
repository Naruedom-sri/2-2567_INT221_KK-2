package intregatedproject.backend.dtos.users;

import lombok.Data;

@Data
public class ResponseSellerDto extends ResponseBuyerDto{
    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
}
