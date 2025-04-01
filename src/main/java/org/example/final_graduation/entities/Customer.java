package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @jakarta.validation.constraints.Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @jakarta.validation.constraints.Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "password", nullable = false)
    private String password = "default_password";

    @jakarta.validation.constraints.Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    @jakarta.validation.constraints.Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @jakarta.validation.constraints.Size(max = 100)
    @Nationalized
    @Column(name = "address", length = 100)
    private String address;

    @jakarta.validation.constraints.Size(max = 255)
    @Nationalized
    @Column(name = "photo")
    private String photo;

    @Column(name = "created_date", nullable = false)
    private java.util.Date createdDate = new java.util.Date();

    @Column(name = "status")
    private boolean status;

}
