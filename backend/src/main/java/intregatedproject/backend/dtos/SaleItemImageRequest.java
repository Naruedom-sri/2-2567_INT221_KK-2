package intregatedproject.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SaleItemImageRequest extends SaleItemImageResponse{
    private MultipartFile ImageFile;
}
