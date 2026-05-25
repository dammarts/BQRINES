package org.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    // CC / NIT / CE
    @Column(length = 50)
    private String document;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceVehicle> vehicles = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
