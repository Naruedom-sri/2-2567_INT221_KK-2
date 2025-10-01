package intregatedproject.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@ToString
@Entity
@Table(name = "sellers")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sellerId", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    private User user;

    @Size(max = 10)
    @NotNull
    @Column(name = "mobileNumber", nullable = false, length = 10)
    private String mobileNumber;

    @Size(max = 50)
    @NotNull
    @Column(name = "bankAccountNumber", nullable = false, length = 50)
    private String bankAccountNumber;

    @Size(max = 50)
    @NotNull
    @Column(name = "bankName", nullable = false, length = 50)
    private String bankName;

    @Size(max = 13)
    @NotNull
    @Column(name = "nationalIdNumber", nullable = false, length = 13)
    private String nationalIdNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "nationalIdPhotoFront", nullable = false)
    private String nationalIdPhotoFront;

    @Size(max = 255)
    @NotNull
    @Column(name = "nationalIdPhotoBack", nullable = false)
    private String nationalIdPhotoBack;


}