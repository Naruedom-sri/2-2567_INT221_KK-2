package intregatedproject.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyerId", nullable = false)
    private User buyer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sellerId", nullable = false)
    private User seller;

    @NotNull
    @Column(name = "orderDate", nullable = false)
    private Instant orderDate;

    @Size(max = 100)
    @NotNull
    @Column(name = "shippingAddress", nullable = false, length = 100)
    private String shippingAddress;

    @Size(max = 100)
    @NotNull
    @Column(name = "orderNote", nullable = false, length = 100)
    private String orderNote;

    @ColumnDefault("'COMPLETED'")
    @Lob
    @Column(name = "orderStatus")
    private String orderStatus;

}