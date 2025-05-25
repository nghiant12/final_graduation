package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "colors")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    public Color() {
    }

    public Color(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @Size(max = 50) @NotNull String getName() {
        return name;
    }

    public void setName(@Size(max = 50) @NotNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Color{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}