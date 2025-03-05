package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.Date;
import java.util.List;

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

    public Customer() {
    }

    public Customer(Integer id, String username, String password, String fullname, String phoneNumber, String email, String address, String photo, Date createdDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
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

    public @Size(max = 255) @NotNull @NotBlank(groups = Customer.class) String getPassword() {
        return password;
    }

    public void setPassword(@Size(max = 255) @NotNull @NotBlank(groups = Customer.class) String password) {
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
}
