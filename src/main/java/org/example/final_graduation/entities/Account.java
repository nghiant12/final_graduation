package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "password", nullable = false)
    @NotBlank(groups = Account.class) // Chỉ kiểm tra khi tạo mới
    private String password;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Size(max = 100)
    @Nationalized
    @Column(name = "address", length = 100)
    private String address;

    @Size(max = 255)
    @Nationalized
    @Column(name = "photo")
    private String photo;

    @Column(name = "created_date", nullable = false)
    private java.util.Date createdDate = new java.util.Date();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Authority> authorities;

}
