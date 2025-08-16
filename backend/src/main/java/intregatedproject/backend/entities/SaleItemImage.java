package intregatedproject.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SaleItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saleItem_id", nullable = false)
    private SaleItem saleItem;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String ogFileName;

    @Column(nullable = false)
    private Integer imageViewOrder;
}
