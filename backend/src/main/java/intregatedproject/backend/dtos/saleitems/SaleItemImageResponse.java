package intregatedproject.backend.dtos.saleitems;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemImageResponse {
    private String fileName;
    private Integer imageViewOrder;
    private String ogFileName;
}
