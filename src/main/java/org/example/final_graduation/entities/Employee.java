package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
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
    @NotBlank(groups = Employee.class) // Chỉ kiểm tra khi tạo mới
    private String password;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

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
    private Date createdDate = new Date();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public Employee() {
    }

    public Employee(Integer id, String username, String password, String fullname, String phoneNumber, String email, String address, String photo, Date createdDate, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.photo = photo;
        this.createdDate = createdDate;
        this.role = role;
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

    public @Size(max = 255) @NotNull @NotBlank(groups = Employee.class) String getPassword() {
        return password;
    }

    public void setPassword(@Size(max = 255) @NotNull @NotBlank(groups = Employee.class) String password) {
        this.password = password;
    }

    public @Size(max = 100) @NotNull String getFullname() {
        return fullname;
    }

    public void setFullname(@Size(max = 100) @NotNull String fullname) {
        this.fullname = fullname;
    }

    public @Size(max = 100) @NotNull String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Size(max = 100) @NotNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public @NotNull Role getRole() {
        return role;
    }

    public void setRole(@NotNull Role role) {
        this.role = role;
    }

    public String getRoleName() {
        return role != null ? role.getName() : "ROLE_USER";
    }

}
