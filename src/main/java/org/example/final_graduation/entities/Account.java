package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.List;
import java.time.LocalDateTime;

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
  
    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    public Account() {
    }

    public Account(Integer id, String username, String password, String fullname, String email, String address, String photo, LocalDateTime createdDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.address = address;
        this.photo = photo;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @Size(max = 50) @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@Size(max = 50) @NotNull String username) {
        this.username = username;
    }

    public @Size(max = 255) @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@Size(max = 255) @NotNull String password) {
        this.password = password;
    }

    public @Size(max = 100) @NotNull String getFullname() {
        return fullname;
    }

    public void setFullname(@Size(max = 100) @NotNull String fullname) {
        this.fullname = fullname;
    }

    public @Size(max = 100) @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@Size(max = 100) @NotNull String email) {
        this.email = email;
    }

    public @Size(max = 100) String getAddress() {
        return address;
    }

    public void setAddress(@Size(max = 100) String address) {
        this.address = address;
    }

    public @Size(max = 255) String getPhoto() {
        return photo;
    }

    public void setPhoto(@Size(max = 255) String photo) {
        this.photo = photo;
    }

    public @NotNull LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NotNull LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
