package intregatedproject.backend.dtos.saleitems;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemImageResponse {
    private String fileName;
    private Integer imageViewOrder;
    private String ogFileName;
}
